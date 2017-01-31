/*
  @className OrderDataConfirmationCommand
  @author 池田大和
  @date 2017/01/31
*/
package command;

import ex.LogicException;
import ex.IntegrationException;
import logic.RequestContext;
import logic.ResponseContext;

import javax.servlet.http.HttpSession;

/*入力された注文情報を確認する画面を表示するコマンドのクラス*/
public class OrderExecutionCommand extends AbstractCommand {
	/*入力された注文情報をセッションスコープに保存し確認画面に渡すメソッド*/
	public ResponseContext execute( ResponseContext responseContext )
	throws LogicException {
		/*initメソッドによって準備されていたRequestContextを取得する*/
		RequestContext requestContext = getRequestContext();
		
		/*注文情報を保存しているセッションを取得する*/
		HttpSession session = (HttpSession)requestContext.getSession();
		
		try{
			/*DAOを取得する*/
			AbstractDaoFactory factory = AbstractDaoFactory.getFactory();
			PurchaseOrderDao purchaseOrderDao = factory.getPurchaseOrderDao();
			
			/*
				以上 01/31
			*/
			
		}
		
		/*転送先のViewの名前をレスポンスに加える*/
		responseContext.setTarget("ordercomp"); 
		
		/*必要な情報を入れ終わったレスポンスを返す*/
		return responseContext;
	}
}