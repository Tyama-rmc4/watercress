/*
  @className OrderDataConfirmationCommand
  @author �r�c��a
  @date 2017/01/31
  @description �������������s����R�}���h�̃N���X�B
  ���������̗���́A
  �܂��u�����v��o�^���A���ɂ���ɕ�������e���i�́u�������ׁv��o�^����B
  �u�����v�́A���̒������s���������ID�⎞�Ԃ̏��ł���A
  �u�������ׁv�́A���̒����Œ������ꂽ���i��ID�ƌ��̏��ł���B
  �u�����v��PurchaseOrder�\�ɁA�u�������ׁv��PurchaseOrderDetail�\�ɓo�^����B
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

/*�������������s����R�}���h�̃N���X*/
public class OrderExecutionCommand extends AbstractCommand {
	/*�������������s���郁�\�b�h*/
	public ResponseContext execute(ResponseContext responseContext)
	throws LogicException {
		/*init���\�b�h�ɂ���ď�������Ă���RequestContext���擾����*/
		RequestContext requestContext = getRequestContext();
		
		/*��������o�^���邽�߂�Bean���C���X�^���X������*/
		PurchaseOrderBean purchaseOrder = new PurchaseOrderBean();
		
		/*�����ID���Z�b�V�����X�R�[�v����擾���ABean�Ɋi�[����*/
		String memberIdAttribute
		= (String)requestContext.getSessionAttribute("login");
		purchaseOrder.setMemberId(Integer.parseInt(memberIdAttribute));
		
		/*�x�������@���Z�b�V�����X�R�[�v����擾���ABean�Ɋi�[����*/
		purchaseOrder.setPurchaseOrderPaymentMethod(
			(String)requestContext.getSessionAttribute("paymentmethod"));
		
		try{
			/*�����̏���o�^���邽�߂�DAO���擾����*/
			AbstractDaoFactory factory = AbstractDaoFactory.getFactory();
			PurchaseOrderDao purchaseOrderDao = factory.getPurchaseOrderDao();
			/*�������ׂ̏���o�^���邽�߂�DAO���擾����*/
			PurchaseOrderDetailDao purchaseOrderDetailDao
			= factory.getPurchaseOrderDetailDao();
			
			/*�����̏���o�^���A�s���������̒���ID���擾����*/
			int purchaseOrderId
			= purchaseOrderDao.setPurchaseOrder(purchaseOrder);
			
			/*�u�����v�̏���o�^�����B���Ɂu�������ׁv�̏���o�^����B*/
			
			/*�������鏤�i��ID�̔z����Z�b�V�����X�R�[�v����擾����*/
			String[] orderProductsId = 
			(String[])requestContext.getSessionAttribute("orderproducts");
			
			/*�������鏤�i�̌��̔z����Z�b�V�����X�R�[�v����擾����*/
			String[] orderCountsAttribute = 
			(String[])requestContext.getSessionAttribute("ordercounts");
			
			/*�������鏤�i�̌��̔z���int�z��^�ɕϊ�����*/
			int[] orderCounts = new int[orderCountsAttribute.length];
			for(int i = 0; i < orderCountsAttribute.length; i++){
				orderCounts[i] = Integer.parseInt(orderCountsAttribute[i]);
			}
			
			/*�������鏤�i�̌���A�������s��*/
			for(int i = 0; i < orderProductsId.length; i++){
				
				/*�������׏���o�^���邽�߂�Bean���C���X�^���X������*/
				PurchaseOrderDetailBean purchaseOrderDetail
				= new PurchaseOrderDetailBean();
				
				/*�o�^����������ID��Bean�Ɋi�[����*/
				purchaseOrderDetail.setPurchaseOrderId(purchaseOrderId);
				
				/*�������鏤�i��ID��Bean�Ɋi�[����*/
				purchaseOrderDetail.setProductId(orderProductsId[i]);
				
				/*�������鏤�i�̌���Bean�Ɋi�[����*/
				purchaseOrderDetail.setPurchaseCount(orderCounts[i]);
				
				/*�������ׂ̏���o�^����*/
				purchaseOrderDetailDao.setPurchaseOrderDetail(
					purchaseOrderDetail);
			}
		}catch(IntegrationException e){
			throw new LogicException(e.getMessage(), e);
		}
		
		/*�]�����View�̖��O�����X�|���X�ɉ�����*/
		responseContext.setTarget("ordercomp"); 
		
		/*�K�v�ȏ������I��������X�|���X��Ԃ�*/
		return responseContext;
	}
}