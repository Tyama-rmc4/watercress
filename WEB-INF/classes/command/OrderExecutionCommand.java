/*
  @className OrderDataConfirmationCommand
  @author 池田大和
  @date 2017/01/31
  @description 注文処理を実行するコマンドのクラス。
  注文処理の流れは、
  まず「注文」を登録し、次にそれに附属する各商品の「注文明細」を登録する。
  「注文」は、その注文を行った会員のIDや時間の情報であり、
  「注文明細」は、その注文で注文された商品のIDと個数の情報である。
  「注文」はPurchaseOrder表に、「注文明細」はPurchaseOrderDetail表に登録する。
*/
package command;

import ex.LogicException;
import ex.IntegrationException;
import logic.RequestContext;
import logic.ResponseContext;
import dao.AbstractDaoFactory;
import dao.PurchaseOrderDao;
import dao.PurchaseOrderDetailDao;
import bean.PurchaseOrderBean;
import bean.PurchaseOrderDetailBean;

/*注文処理を実行するコマンドのクラス*/
public class OrderExecutionCommand extends AbstractCommand {
	/*注文処理を実行するメソッド*/
	public ResponseContext execute(ResponseContext responseContext)
	throws LogicException {
		/*initメソッドによって準備されていたRequestContextを取得する*/
		RequestContext requestContext = getRequestContext();
		
		/*注文情報を登録するためのBeanをインスタンス化する*/
		PurchaseOrderBean purchaseOrder = new PurchaseOrderBean();
		
		/*会員のIDをセッションスコープから取得し、Beanに格納する*/
		String memberIdAttribute
		= (String)requestContext.getSessionAttribute("login");
		purchaseOrder.setMemberId(Integer.parseInt(memberIdAttribute));
		
		/*支払い方法をセッションスコープから取得し、Beanに格納する*/
		purchaseOrder.setPurchaseOrderPaymentMethod(
			(String)requestContext.getSessionAttribute("paymentmethod"));
		
		try{
			/*注文の情報を登録するためのDAOを取得する*/
			AbstractDaoFactory factory = AbstractDaoFactory.getFactory();
			PurchaseOrderDao purchaseOrderDao = factory.getPurchaseOrderDao();
			/*注文明細の情報を登録するためのDAOを取得する*/
			PurchaseOrderDetailDao purchaseOrderDetailDao
			= factory.getPurchaseOrderDetailDao();
			
			/*注文の情報を登録し、行った注文の注文IDを取得する*/
			int purchaseOrderId
			= purchaseOrderDao.setPurchaseOrder(purchaseOrder);
			
			/*「注文」の情報を登録した。次に「注文明細」の情報を登録する。*/
			
			/*注文する商品のIDの配列をセッションスコープから取得する*/
			String[] orderProductsId = 
			(String[])requestContext.getSessionAttribute("orderproducts");
			
			/*注文する商品の個数の配列をセッションスコープから取得する*/
			String[] orderCountsAttribute = 
			(String[])requestContext.getSessionAttribute("ordercounts");
			
			/*注文する商品の個数の配列をint配列型に変換する*/
			int[] orderCounts = new int[orderCountsAttribute.length];
			for(int i = 0; i < orderCountsAttribute.length; i++){
				orderCounts[i] = Integer.parseInt(orderCountsAttribute[i]);
			}
			
			/*注文する商品の個数回、処理を行う*/
			for(int i = 0; i < orderProductsId.length; i++){
				
				/*注文明細情報を登録するためのBeanをインスタンス化する*/
				PurchaseOrderDetailBean purchaseOrderDetail
				= new PurchaseOrderDetailBean();
				
				/*登録した注文のIDをBeanに格納する*/
				purchaseOrderDetail.setPurchaseOrderId(purchaseOrderId);
				
				/*注文する商品のIDをBeanに格納する*/
				purchaseOrderDetail.setProductId(orderProductsId[i]);
				
				/*注文する商品の個数をBeanに格納する*/
				purchaseOrderDetail.setPurchaseCount(orderCounts[i]);
				
				/*注文明細の情報を登録する*/
				purchaseOrderDetailDao.setPurchaseOrderDetail(
					purchaseOrderDetail);
			}
		}catch(IntegrationException e){
			throw new LogicException(e.getMessage(), e);
		}
		
		/*転送先のViewの名前をレスポンスに加える*/
		responseContext.setTarget("ordercomp"); 
		
		/*必要な情報を入れ終わったレスポンスを返す*/
		return responseContext;
	}
}