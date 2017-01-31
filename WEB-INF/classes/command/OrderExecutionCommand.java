/*
  @className OrderDataConfirmationCommand
  @author �r�c��a
  @date 2017/01/31
*/
package command;

import ex.LogicException;
import ex.IntegrationException;
import logic.RequestContext;
import logic.ResponseContext;

import javax.servlet.http.HttpSession;

/*���͂��ꂽ���������m�F�����ʂ�\������R�}���h�̃N���X*/
public class OrderExecutionCommand extends AbstractCommand {
	/*���͂��ꂽ���������Z�b�V�����X�R�[�v�ɕۑ����m�F��ʂɓn�����\�b�h*/
	public ResponseContext execute( ResponseContext responseContext )
	throws LogicException {
		/*init���\�b�h�ɂ���ď�������Ă���RequestContext���擾����*/
		RequestContext requestContext = getRequestContext();
		
		/*��������ۑ����Ă���Z�b�V�������擾����*/
		HttpSession session = (HttpSession)requestContext.getSession();
		
		try{
			/*DAO���擾����*/
			AbstractDaoFactory factory = AbstractDaoFactory.getFactory();
			PurchaseOrderDao purchaseOrderDao = factory.getPurchaseOrderDao();
			
			/*
				�ȏ� 01/31
			*/
			
		}
		
		/*�]�����View�̖��O�����X�|���X�ɉ�����*/
		responseContext.setTarget("ordercomp"); 
		
		/*�K�v�ȏ������I��������X�|���X��Ԃ�*/
		return responseContext;
	}
}