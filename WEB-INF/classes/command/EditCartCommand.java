package command;

import ex.LogicException;
import ex.IntegrationException;

import logic.RequestContext;
import logic.ResponseContext;

/**
 *@className EditCartCommand
 *@author ���V
 *@date 2017/01/31
 *@description 
 */
/*
�J�[�g�ɓo�^���ꂽ���i���폜����Command
*/
public class EditCartCommand extends AbstractCommand{

	public EditCartCommand(){}
	
	public ResponseContext execute( ResponseContext responseContext )
	throws LogicException{
		RequestContext req = getRequestContext();
		
		
		String listNumberAttribute = req.getParameter("listNumber")[0];
		
		/*
		�J�[�g���̏��i���폜����ۂɁA�폜���鏤�i���I�΂�Ă��Ȃ���null���Ԃ�
		�Ή����邽�߂Ɉ�Unull�����String������
		*/
		String checkAttribute = listNumberAttribute;
		int checkNumber=0;
/*
		���g��null�̏ꍇ0�ł͂Ȃ�-1�ŏ㏑������
		*/
		if(checkAttribute == null){
			checkNumber = -1;
		}
		
		/*
		��Őݒ肵��null�ꍇ��-1��������A�J�[�g���ɏ��i���܂����݂��Ȃ��̂�
		�폜���镶�����s�����Ȃ��B
		*/
		int listNumber = -1;
		
		if(0 <= checkNumber){
			listNumber = Integer.parseInt(listNumberAttribute);
		}else{
			System.out.println("�폜���鏤�i��I�����ĂˁI");
		}
		
		
		
		String removeResultNumber = "result" + listNumberAttribute;
		
		req.removeSessionAttribute(removeResultNumber);
		
		responseContext.setTarget("cart");
		
		return responseContext;
	}
}

