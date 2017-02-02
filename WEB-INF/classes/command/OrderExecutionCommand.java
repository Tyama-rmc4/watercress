/*
  @className OrderDataConfirmationCommand
  @author �r�c��a
  @date 2017/01/31
  @description �������������s����R�}���h�̃N���X�B
  ���������̗���́A
  �܂��������鏤�i�̍݌ɂ����邱�Ƃ��m�F����B
  ���Ɂu�����v��o�^���A����ɕ�������e���i�́u�������ׁv��o�^����B
  �u�����v�́A���̒������s���������ID�⎞�Ԃ̏��ł���A
  �u�������ׁv�́A���̒����Œ������ꂽ���i��ID�ƌ��̏��ł���B
  �u�����v��PurchaseOrder�\�ɁA�u�������ׁv��PurchaseOrderDetail�\�ɓo�^����B
  �Ō�ɁA�����������i�̍݌ɂ����炷�B
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

/*�������������s����R�}���h�̃N���X*/
public class OrderExecutionCommand extends AbstractCommand {
	
	/*RequestContext���i�[����C���X�^���X�ϐ�*/
	RequestContext requestContext;
	
	/*���L�̂R�̕������\�b�h���Ăяo���A�������������s���郁�\�b�h*/
	public ResponseContext execute(ResponseContext responseContext)
	throws LogicException {
		/*init���\�b�h�ɂ���ď�������Ă���RequestContext���擾����*/
		requestContext = getRequestContext();
		
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
		
		try{
			/*�܂��A�݌ɐ��̊m�F���s���B*/
			checkProductStocks(orderProductsId,orderCounts);
			
			/*���Ɂu�����v�Ɓu�������ׁv�̏���o�^����B*/
			registOrder(orderProductsId,orderCounts);
			
			/*�Ō�ɍ݌ɐ��̕ύX���s���B*/
			updateProductStocks(orderProductsId,orderCounts);
			
		}catch(LogicException e){
			throw new LogicException(e.getMessage(), e);
		}
		
		/*�]�����View�̖��O�����X�|���X�ɉ�����*/
		responseContext.setTarget("ordercomp"); 
		
		/*�K�v�ȏ������I��������X�|���X��Ԃ�*/
		return responseContext;
	}
	
	/*�݌ɐ��̊m�F���s�����\�b�h*/
	private void checkProductStocks(String[] orderProductsId,int[] orderCounts)
	throws LogicException {
		
		try{
			/*�݌ɂ̏��̊m�F�Ɠo�^���s�����߂�DAO���擾����*/
			AbstractDaoFactory factory = AbstractDaoFactory.getFactory();
			ProductStockDao productStockDao = factory.getProductStockDao();
			
			/*�݌ɂ̏����擾����*/
			List productStocks = productStockDao.getProductStocks();
			
			/*�����������i�̍݌ɐ����i�[����ϐ���錾����*/
			int[] orderProductStockCounts = new int[orderCounts.length];
			
			/*
				����
				orderProductsId			�������鏤�i��ID�̔z��
				orderCounts				����������̔z��
				orderProductStockCounts	�������鏤�i�̍݌ɐ��̔z��
			*/
			
			/*�݌ɂ̃��X�g�̊e�s���m�F���ď������s��*/
			Iterator iterator = productStocks.iterator();
			while(iterator.hasNext()){
				
				/*�݌Ƀ��X�g�̂����̂P�s�ł���Bean���擾����*/
				ProductStockBean productStock
				= (ProductStockBean)iterator.next();
				
				/*����Bean���珤�iID���擾����*/
				String productId = productStock.getProductId();
				
				/*�������鏤�i�̍݌ɐ����擾���鏈�����s���B*/
				/*�������鏤�i��ID�̔z��̗v�f�����A���[�v����B*/
				for(int i = 0; i < orderProductsId.length; i++){
					
					/*���݂�Bean�̏��iID���A
					�������鏤�i��ID�̔z��i�ԖڂƓ����ł���Ȃ�A*/
					if(productId.equals(orderProductsId[i])){
						
						/*�������鏤�i�̍݌ɐ��̔z��i�ԖڂɁA
						���݂�Bean�̍݌ɐ���������B*/
						orderProductStockCounts[i]
						= productStock.getProductStockCount();
					}
				}
			}
			
			/*�u����������v�Ɓu�������鏤�i�̍݌ɐ��v���r����*/
			for(int i = 0; i < orderCounts.length; i++){
				/*������������������鏤�i�̍݌ɐ���葽���Ȃ��O���o*/
				if(orderCounts[i] > orderProductStockCounts[i]){
					throw new LogicException(
						orderProductsId[i]+"�̍݌ɂ�����܂���B",null);
				}
			}
			
		}catch(IntegrationException e){
			throw new LogicException(e.getMessage(), e);
		}
		
	}
	
	/*�u�����v�Ɓu�������ׁv�̏���o�^���郁�\�b�h*/
	private void registOrder(String[] orderProductsId,int[] orderCounts)
	throws LogicException {
		try{
			/*�����̏���o�^���邽�߂�DAO���擾����*/
			AbstractDaoFactory factory = AbstractDaoFactory.getFactory();
			PurchaseOrderDao purchaseOrderDao = factory.getPurchaseOrderDao();
			
			/*��������o�^���邽�߂�Bean���C���X�^���X������*/
			PurchaseOrderBean purchaseOrder = new PurchaseOrderBean();
			
			/*�����ID���Z�b�V�����X�R�[�v����擾���ABean�Ɋi�[����*/
			String memberIdAttribute
			= (String)requestContext.getSessionAttribute("login");
			purchaseOrder.setMemberId(Integer.parseInt(memberIdAttribute));
			
			/*�x�������@���Z�b�V�����X�R�[�v����擾���ABean�Ɋi�[����*/
			purchaseOrder.setPurchaseOrderPaymentMethod(
				(String)requestContext.getSessionAttribute("paymentmethod"));
			
			/*�����̏���o�^���A�s���������̒���ID���擾����*/
			int purchaseOrderId
			= purchaseOrderDao.setPurchaseOrder(purchaseOrder);
			
			/*�������ׂ̏���o�^���邽�߂�DAO���擾����*/
			PurchaseOrderDetailDao purchaseOrderDetailDao
			= factory.getPurchaseOrderDetailDao();
			
			/*�������鏤�i�̎�ސ���A�������s��*/
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
	}
	
	/*�݌ɐ��̕ύX���s�����\�b�h*/
	private void updateProductStocks(String[] orderProductsId,
		int[] orderCounts)
	throws LogicException {
		try{
			/*�݌ɂ̏��̊m�F�Ɠo�^���s�����߂�DAO���擾����*/
			AbstractDaoFactory factory = AbstractDaoFactory.getFactory();
			ProductStockDao productStockDao = factory.getProductStockDao();
			
			/*�݌ɂ̏����擾����*/
			List productStocks = productStockDao.getProductStocks();
			
			/*�݌ɂ̃��X�g�̊e�s���m�F���ď������s��*/
			Iterator iterator = productStocks.iterator();
			while(iterator.hasNext()){
				
				/*�݌Ƀ��X�g�̂����̂P�s�ł���Bean���擾����*/
				ProductStockBean productStock
				= (ProductStockBean)iterator.next();
				
				/*����Bean���珤�iID���擾����*/
				String productId = productStock.getProductId();
				
				/*�����������i�̍݌ɐ����擾���鏈�����s���B*/
				/*�����������i��ID�̔z��̗v�f�����A���[�v����B*/
				for(int i = 0; i < orderProductsId.length; i++){
					
					/*���݂�Bean�̏��iID���A
					�����������i��ID�̔z��i�ԖڂƓ����ł���Ȃ�A*/
					if(productId.equals(orderProductsId[i])){
						
						/*���݂�Bean�̍݌ɐ����A���������������炵�A*/
						productStock.setProductStockCount(
							productStock.getProductStockCount()
							- orderCounts[i]
						);
						
						/*����Bean�̓��e�̒ʂ�ɍ݌ɂ̕\���X�V����B*/
						productStockDao.setProductStock(productStock);
					}
				}
			}
		}catch(IntegrationException e){
			throw new LogicException(e.getMessage(), e);
		}
	}
}