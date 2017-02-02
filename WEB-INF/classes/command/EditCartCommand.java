package command;

import ex.LogicException;
import ex.IntegrationException;

import logic.RequestContext;
import logic.ResponseContext;

/**
 *@className EditCartCommand
 *@author 塩澤
 *@date 2017/01/31
 *@description 
 */
/*
カートに登録された商品を削除するCommand
*/
public class EditCartCommand extends AbstractCommand{

	public EditCartCommand(){}
	
	public ResponseContext execute( ResponseContext responseContext )
	throws LogicException{
		RequestContext req = getRequestContext();
		
		
		String listNumberAttribute = req.getParameter("listNumber")[0];
		
		/*
		カート内の商品を削除する際に、削除する商品が選ばれていないとnullが返る
		対応するために一旦null判定のStringを挟む
		*/
		String checkAttribute = listNumberAttribute;
		int checkNumber=0;
/*
		中身がnullの場合0ではなく-1で上書きする
		*/
		if(checkAttribute == null){
			checkNumber = -1;
		}
		
		/*
		上で設定したnull場合の-1がきたら、カート内に商品がまだ存在しないので
		削除する文を実行させない。
		*/
		int listNumber = -1;
		
		if(0 <= checkNumber){
			listNumber = Integer.parseInt(listNumberAttribute);
		}else{
			System.out.println("削除する商品を選択してね！");
		}
		
		
		
		String removeResultNumber = "result" + listNumberAttribute;
		
		req.removeSessionAttribute(removeResultNumber);
		
		responseContext.setTarget("cart");
		
		return responseContext;
	}
}

