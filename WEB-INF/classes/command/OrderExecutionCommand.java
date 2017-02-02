/*
  @className OrderDataConfirmationCommand
  @author 池田大和
  @date 2017/01/31
  @description 注文処理を実行するコマンドのクラス。
  注文処理の流れは、
  まず注文する商品の在庫があることを確認する。
  次に「注文」を登録し、それに附属する各商品の「注文明細」を登録する。
  「注文」は、その注文を行った会員のIDや時間の情報であり、
  「注文明細」は、その注文で注文された商品のIDと個数の情報である。
  「注文」はPurchaseOrder表に、「注文明細」はPurchaseOrderDetail表に登録する。
  最後に、注文した商品の在庫を減らす。
*/
package command;

import java.util.List;
import java.util.Iterator;

import ex.LogicException;
import ex.IntegrationException;
import logic.RequestContext;
import logic.ResponseContext;
import dao.AbstractDaoFactory;
import dao.PurchaseOrderDao;
import dao.PurchaseOrderDetailDao;
import dao.ProductStockDao;
import bean.PurchaseOrderBean;
import bean.PurchaseOrderDetailBean;
import bean.ProductStockBean;

/*注文処理を実行するコマンドのクラス*/
public class OrderExecutionCommand extends AbstractCommand {
	
	/*RequestContextを格納するインスタンス変数*/
	RequestContext requestContext;
	
	/*下記の３つの分割メソッドを呼び出し、注文処理を実行するメソッド*/
	public ResponseContext execute(ResponseContext responseContext)
	throws LogicException {
		/*initメソッドによって準備されていたRequestContextを取得する*/
		requestContext = getRequestContext();
		
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
		
		try{
			/*まず、在庫数の確認を行う。*/
			checkProductStocks(orderProductsId,orderCounts);
			
			/*次に「注文」と「注文明細」の情報を登録する。*/
			registOrder(orderProductsId,orderCounts);
			
			/*最後に在庫数の変更を行う。*/
			updateProductStocks(orderProductsId,orderCounts);
			
		}catch(LogicException e){
			throw new LogicException(e.getMessage(), e);
		}
		
		/*転送先のViewの名前をレスポンスに加える*/
		responseContext.setTarget("ordercomp"); 
		
		/*必要な情報を入れ終わったレスポンスを返す*/
		return responseContext;
	}
	
	/*在庫数の確認を行うメソッド*/
	private void checkProductStocks(String[] orderProductsId,int[] orderCounts)
	throws LogicException {
		
		try{
			/*在庫の情報の確認と登録を行うためのDAOを取得する*/
			AbstractDaoFactory factory = AbstractDaoFactory.getFactory();
			ProductStockDao productStockDao = factory.getProductStockDao();
			
			/*在庫の情報を取得する*/
			List productStocks = productStockDao.getProductStocks();
			
			/*注文した商品の在庫数を格納する変数を宣言する*/
			int[] orderProductStockCounts = new int[orderCounts.length];
			
			/*
				注意
				orderProductsId			注文する商品のIDの配列
				orderCounts				注文する個数の配列
				orderProductStockCounts	注文する商品の在庫数の配列
			*/
			
			/*在庫のリストの各行を確認して処理を行う*/
			Iterator iterator = productStocks.iterator();
			while(iterator.hasNext()){
				
				/*在庫リストのうちの１行であるBeanを取得する*/
				ProductStockBean productStock
				= (ProductStockBean)iterator.next();
				
				/*そのBeanから商品IDを取得する*/
				String productId = productStock.getProductId();
				
				/*注文する商品の在庫数を取得する処理を行う。*/
				/*注文する商品のIDの配列の要素数分、ループする。*/
				for(int i = 0; i < orderProductsId.length; i++){
					
					/*現在のBeanの商品IDが、
					注文する商品のIDの配列i番目と同じであるなら、*/
					if(productId.equals(orderProductsId[i])){
						
						/*注文する商品の在庫数の配列i番目に、
						現在のBeanの在庫数を代入する。*/
						orderProductStockCounts[i]
						= productStock.getProductStockCount();
					}
				}
			}
			
			/*「注文する個数」と「注文する商品の在庫数」を比較する*/
			for(int i = 0; i < orderCounts.length; i++){
				/*注文する個数が注文する商品の在庫数より多いなら例外送出*/
				if(orderCounts[i] > orderProductStockCounts[i]){
					throw new LogicException(
						orderProductsId[i]+"の在庫が足りません。",null);
				}
			}
			
		}catch(IntegrationException e){
			throw new LogicException(e.getMessage(), e);
		}
		
	}
	
	/*「注文」と「注文明細」の情報を登録するメソッド*/
	private void registOrder(String[] orderProductsId,int[] orderCounts)
	throws LogicException {
		try{
			/*注文の情報を登録するためのDAOを取得する*/
			AbstractDaoFactory factory = AbstractDaoFactory.getFactory();
			PurchaseOrderDao purchaseOrderDao = factory.getPurchaseOrderDao();
			
			/*注文情報を登録するためのBeanをインスタンス化する*/
			PurchaseOrderBean purchaseOrder = new PurchaseOrderBean();
			
			/*会員のIDをセッションスコープから取得し、Beanに格納する*/
			String memberIdAttribute
			= (String)requestContext.getSessionAttribute("login");
			purchaseOrder.setMemberId(Integer.parseInt(memberIdAttribute));
			
			/*支払い方法をセッションスコープから取得し、Beanに格納する*/
			purchaseOrder.setPurchaseOrderPaymentMethod(
				(String)requestContext.getSessionAttribute("paymentmethod"));
			
			/*注文の情報を登録し、行った注文の注文IDを取得する*/
			int purchaseOrderId
			= purchaseOrderDao.setPurchaseOrder(purchaseOrder);
			
			/*注文明細の情報を登録するためのDAOを取得する*/
			PurchaseOrderDetailDao purchaseOrderDetailDao
			= factory.getPurchaseOrderDetailDao();
			
			/*注文する商品の種類数回、処理を行う*/
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
	}
	
	/*在庫数の変更を行うメソッド*/
	private void updateProductStocks(String[] orderProductsId,
		int[] orderCounts)
	throws LogicException {
		try{
			/*在庫の情報の確認と登録を行うためのDAOを取得する*/
			AbstractDaoFactory factory = AbstractDaoFactory.getFactory();
			ProductStockDao productStockDao = factory.getProductStockDao();
			
			/*在庫の情報を取得する*/
			List productStocks = productStockDao.getProductStocks();
			
			/*在庫のリストの各行を確認して処理を行う*/
			Iterator iterator = productStocks.iterator();
			while(iterator.hasNext()){
				
				/*在庫リストのうちの１行であるBeanを取得する*/
				ProductStockBean productStock
				= (ProductStockBean)iterator.next();
				
				/*そのBeanから商品IDを取得する*/
				String productId = productStock.getProductId();
				
				/*注文した商品の在庫数を取得する処理を行う。*/
				/*注文した商品のIDの配列の要素数分、ループする。*/
				for(int i = 0; i < orderProductsId.length; i++){
					
					/*現在のBeanの商品IDが、
					注文した商品のIDの配列i番目と同じであるなら、*/
					if(productId.equals(orderProductsId[i])){
						
						/*現在のBeanの在庫数を、注文した個数分減らし、*/
						productStock.setProductStockCount(
							productStock.getProductStockCount()
							- orderCounts[i]
						);
						
						/*そのBeanの内容の通りに在庫の表を更新する。*/
						productStockDao.setProductStock(productStock);
					}
				}
			}
		}catch(IntegrationException e){
			throw new LogicException(e.getMessage(), e);
		}
	}
}