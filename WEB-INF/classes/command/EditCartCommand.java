package command;

import ex.LogicException;
import ex.IntegrationException;

import logic.RequestContext;
import logic.ResponseContext;

import java.util.Map;
import java.util.HashMap;

/**
 *@className EditCartCommand
 *@author ���V
 *@date 2017/01/31
 *@description 
 */
/*
�J�[�g�ɓo�^���ꂽ���i�́A�����������ύX����Command
*/
public class EditCartCommand extends AbstractCommand{

	public EditCartCommand(){}
	
	public ResponseContext execute( ResponseContext responseContext )
	throws LogicException{
		RequestContext req = getRequestContext();
		
		/*
		�폜���鏤�i���I�����ꂽ
		aLotOf�E�E�E���������
		*/
		String productId = req.getParameter("productid")[0];
		String itemCount =req.getParameter("itemcount")[0];
		Map<String,String> cart = (Map<String,String>)req.getSessionAttribute("cart");
		
		if(cart.containsKey(productId) == true){
			cart.put(productId,itemCount);
		}
		
		
		responseContext.setTarget("cart");
		
		return responseContext;
	}
}

