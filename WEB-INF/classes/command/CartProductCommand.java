package command;

import ex.LogicException;
import ex.IntegrationException;

import logic.RequestContext;
import logic.WebRequestContext;
import logic.ResponseContext;


/**
 *@className CartProductCommand
 *@author ���V
 *@date 2017/01/31
 *@description 
 */
public class CartProductCommand extends AbstractCommand{
	
	public CartProductCommand(){}
	
	public ResponseContext execute( ResponseContext responseContext )
	throws LogicException{
		
		RequestContext req = new WebRequestContext();
		
		/*���͂��ꂽ�p�����[�^���󂯎��*/
		String[] productId = req.getParameter("productid");
		
		/*�Z�b�V�����X�R�[�v�ɈႤ�C���X�^���X��ۑ����邽�߂̕ϐ�*/
		int maxProductCount; 
		
		/*����̃��N�G�X�g����maxNum�̒l��Null�����肷��*/
		/*Null�łȂ��ꍇ��maxNum�ɂ��̒l������*/
		if(req.getSessionAttribute("maxProductCount") != null){
			maxProductCount = (int)req.getSessionAttribute("maxProductCount");
		}else{
			maxProductCount = 0;
		}
		
		/*�Z�b�V�����X�R�[�v�ɓ��͂��ꂽ�p�����[�^���������C���X�^���X��ۑ�*/
		req.setSessionAttribute("result" + maxProductCount, productId);
			/*�Z�b�V�����X�R�[�v��+1����maxNum��ۑ�*/
		req.setSessionAttribute("maxProductCount", maxProductCount + 1);
		
		responseContext.setTarget("cart");
		
		return responseContext;
	}
}