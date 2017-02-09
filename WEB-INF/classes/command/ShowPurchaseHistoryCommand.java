package command;

import java.util.List;
import java.util.Iterator;

import java.io.IOException;

import bean.ProductBean;
import bean.ProductImageBean;
import bean.PurchaseHistoryBean;

import dao.AbstractDaoFactory;
import dao.ProductDao;
import dao.ProductImageDao;
import dao.PurchaseHistoryDao;

import logic.ResponseContext;
import logic.RequestContext;
import logic.WebRequestContext;

import ex.IntegrationException;
import ex.LogicException;

/**
 *@className ShowPurchaseHistoryCommand
 *@author �͖�
 *@date 2017/02/08
 *@description �w��������\��
 */

public class ShowPurchaseHistoryCommand extends AbstractCommand{
	
	public ResponseContext execute(ResponseContext responseContext)
	throws LogicException{
		/*RequestContext�̃C���X�^���X���擾*/
		RequestContext requestContext = new WebRequestContext();
		
		/*getParameter�Ńy�[�W�ԍ����擾*/
		int pageNum = 0;
		String cushionPage = requestContext.getParameter("pageNumber")[0];
		if(cushionPage != null){
			pageNum = Integer.parseInt(requestContext.getParameter("pageNumber")[0]);
		}
		
		
		/*memberId���󂯎��*/
		int memberId = Integer.parseInt(requestContext.getSessionAttribute("login").toString()) ;
		
		/*�J�E���g�p�̕ϐ�*/
		int i=0;
		
		/*return�ŕԂ����߂̍w�����X�g*/
		List memberPurchaseHistory = null;
		
		/*return�ŕԂ����߂�Product���X�g*/
		List memberProducts = null;
		
		/*return�ŕԂ����߂�ProductsImage���X�g*/
		List memberProductsImage = null;
		
		/*Product�\�̃��X�g��S���擾�̂��߂̃��X�g*/
		List allProducts = null;
		
		/*�w�����X�g��S���擾�̂��߂̃��X�g*/
		List allPurchaseProducts = null;
		
		/*ProductImage���X�g��S���擾�̂��߂̃��X�g*/
		List allProductsImage = null;
		
		try{
			/*AbstractDaoFactory�̃C���X�^���X���擾*/
			AbstractDaoFactory factory = AbstractDaoFactory.getFactory();
			
			/*ProductDao�̃C���X�^���X���擾*/
			ProductDao productDao = factory.getProductDao();
			
			/*PurchaseHistoryDao�̃C���X�^���X���擾*/
			PurchaseHistoryDao purchaseHistoryDao 
			= factory.getPurchaseHistoryDao();
			
			/*ProductImageDao�̃C���X�^���X���擾*/
			ProductImageDao productImage = factory.getProductImageDao();
			
			/*Products���X�g��S���擾*/
			allProducts = productDao.getProducts();
			
			/*�w�����X�g��S���擾*/
			allPurchaseProducts = purchaseHistoryDao.getPurchaseHistories();
			
			/*ProductsImage���X�g��S���擾*/
			allProductsImage = productImage.getProductImages();
			
			Iterator allPurchaseIterator =  allPurchaseProducts.iterator();
			
			while(allPurchaseIterator.hasNext()){
				
				// purchasehistorybean�ɓ����
				PurchaseHistoryBean memberHistoryBean 
				= (PurchaseHistoryBean)allPurchaseIterator.next();
				
				if((pageNum - 1) * 10 <= i && i < pageNum * 10){
					/*memberId�Ɉ�v���郁���o�[�ōw�����X�g���i��*/
					if(memberId == memberHistoryBean.getMemberId()){
						memberPurchaseHistory.add(memberHistoryBean);
						
					}
				}
				i++;
			}
			
			/*�S���i���X�g�̃C�e���[�^*/
			Iterator allProductsIterator = allProducts.iterator();
			/*10�����̍w�����X�g�̃C�e���[�^*/
			Iterator historiesIterator = memberPurchaseHistory.iterator();
			/*�S���iImage���X�g�̃C�e���[�^*/
			Iterator productsImageIterator = allProductsImage.iterator();
			while(allProductsIterator.hasNext()){
				
				/*Product���X�g���ꌏ���i�[*/
				ProductBean productBean = (ProductBean)allProductsIterator.next();
				
				/*���i��ID��productId�̕ϐ��Ɋi�[*/
				String productId = productBean.getProductId();
				
				while(historiesIterator.hasNext()){
					/*�w�����X�g���炷�ׂĂ̏��iID���i�[*/
					String purchaseProductId 
					= ((PurchaseHistoryBean)historiesIterator.next())
					.getProductId();
						if(purchaseProductId == productId){
							memberProducts.add(productBean);
							break;
						}
					}
				}
			
			while(productsImageIterator.hasNext()){
				
				/*ProductImage���X�g���ꌏ���i�[*/
					ProductImageBean productsImageBean 
					= (ProductImageBean)productsImageIterator.next();
				
				/*���i��ID��productImageId�̕ϐ��Ɋi�[*/
					String productImageId = productsImageBean.getProductId();
				
				while(historiesIterator.hasNext()){
					/*�w�����X�g���炷�ׂĂ̏��iID���i�[*/
					String purchaseProductId 
					= ((PurchaseHistoryBean)historiesIterator.next())
					.getProductId();
					if(purchaseProductId == productImageId){
						memberProductsImage.add(productsImageBean);
						break;
					}
				}
			}
		}catch(IntegrationException e){
			throw new LogicException(e.getMessage(), e);
		}
		
		/*���N�G�X�g�X�R�[�v�ɃC���X�^���X��ۑ�*/
		requestContext.setRequestAttribute("products",memberProducts);
		requestContext.setRequestAttribute("purchaseProducts",memberPurchaseHistory);
		requestContext.setRequestAttribute("productsImage",memberProductsImage);
		/*�Z�b�V�����X�R�[�v�ɃC���X�^���X��ۑ�*/
		requestContext.setSessionAttribute("pageNum",pageNum);
		
		/*�]����̃r���[���w��*/
		responseContext.setTarget("orderhistory");
		
		/*return�Ō��ʂ�Ԃ�*/
		return responseContext;
	}
}