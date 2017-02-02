package command;

import ex.LogicException;
import ex.IntegrationException;

import logic.RequestContext;
import logic.WebRequestContext;
import logic.ResponseContext;


/**
 *@className CartProductCommand
 *@author 塩澤
 *@date 2017/01/31
 *@description 
 */
public class CartProductCommand extends AbstractCommand{
	
	public CartProductCommand(){}
	
	public ResponseContext execute( ResponseContext responseContext )
	throws LogicException{
		
		RequestContext req = new WebRequestContext();
		
		/*入力されたパラメータを受け取る*/
		String[] productId = req.getParameter("productid");
		
		/*セッションスコープに違うインスタンスを保存するための変数*/
		int maxProductCount; 
		
		/*初回のリクエスト時にmaxNumの値がNullか判定する*/
		/*Nullでない場合はmaxNumにその値を入れる*/
		if(req.getSessionAttribute("maxProductCount") != null){
			maxProductCount = (int)req.getSessionAttribute("maxProductCount");
		}else{
			maxProductCount = 0;
		}
		
		/*セッションスコープに入力されたパラメータが入ったインスタンスを保存*/
		req.setSessionAttribute("result" + maxProductCount, productId);
			/*セッションスコープに+1したmaxNumを保存*/
		req.setSessionAttribute("maxProductCount", maxProductCount + 1);
		
		responseContext.setTarget("cart");
		
		return responseContext;
	}
}