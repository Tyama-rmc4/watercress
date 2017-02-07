package command;

import java.util.List;
import java.util.Iterator;

import java.io.IOException;
import javax.servlet.http.HttpSession;

import bean.ProductCatalogBean;
import bean.SubCategoryBean;
import dao.ProductCatalogDao;
import dao.OraProductCatalogDao;
import dao.SubCategoryDao;
import dao.AbstractDaoFactory;

import logic.ResponseContext;
import logic.RequestContext;
import logic.WebRequestContext;
import ex.IntegrationException;
import ex.LogicException;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import bean.TagBean;
import dao.TagDao;

/**
 *@className ShowProductsListCommand
 *@author �͖�
 *@date 2017/01/31 �y�[�W�ԍ����Z�b�V�����ŊǗ�����C�������܂����B
 *@author �r�c
 *@date 2017/02/07 �i�[���錋�ʂ̎d�l���A�^�O�̖��O���܂ނ��̂ɕύX���܂����B
 *@description 
 data : List<Map> �yjsp��${data}�Ŏ��o����镔���z
 ��
 ��productData : Map<String, Object>
   ��
   ��"productCatalog",ProductCatalogBean
   ��"tagNames",List<String>
 */

public class ShowProductsListCommand extends AbstractCommand{
	
	public ResponseContext execute(ResponseContext responseContext) throws LogicException{
		
		/*RequestContext�̃C���X�^���X���擾*/
		RequestContext requestContext = new WebRequestContext();
		
		/*�y�[�W�̔ԍ�*/
		int pageNum = Integer.parseInt(requestContext.getParameter("pageNumber")[0]);
		
		/*�I�����ꂽ�T�u�J�e�S��*/
		String selectedSubCategory = requestContext.getParameter("subCategory")[0];
		
		int selectedSubCategoryId = 0;
		
		/*�\�[�g�p�̕ϐ�*/
		String[] sortParams = requestContext.getParameter("sort");
		
		/*getProductCatalogs�ɓn������*/
		int[] sortArray = new int[3];
		
		/*�S�T�u�J�e�S���̏��̃��X�g*/
		List allSubCategoryList = null; 
		
		/*�S���i�̏��̃��X�g*/
		List allCatalogList = null; 
		
		/*�S�^�O�̏��̃��X�g*/
		List allTagList = null;
		
		/*�Ԃ���鏤�i���Ƃ��̃^�O���̃��X�g*/
		List<Map> returnProductsDataList = new ArrayList<Map>();
		
		for(int i = 0;i<=sortParams.length;i++){
			
			/*sortParams�̒��g�ɑΉ�����������ɂ��\�[�g����*/
			/*
			  priceAsc = �l�i�̈�����
			  priceDesc = �l�i�̍�����
			  purchaseAsc = �w���������Ȃ���
			  purchaseDesc = �w������������
			  nameAsc = 50����
			  nameDesc = 50�����̋t�� 
			*/
			if(sortParams[i].equals("priceAsc")){
				sortArray[0] = ProductCatalogDao.SORT_BY_PRICE_ASC;
			}
			else if(sortParams[i].equals("priceDesc")){
				sortArray[0] = ProductCatalogDao.SORT_BY_PRICE_DESC;
			}
			else if(sortParams[i].equals("purchaseAsc")){
				sortArray[1] = ProductCatalogDao.SORT_BY_PURCHASE_COUNT_ASC;
			}
			else if(sortParams[i].equals("purchaseDesc")){
				sortArray[1] = ProductCatalogDao.SORT_BY_PURCHASE_COUNT_DESC;
			}
			else if(sortParams[i].equals("nameAsc")){
				sortArray[2] = ProductCatalogDao.SORT_BY_NAME_ASC;
			}
			else if(sortParams[i].equals("nameDesc")){
				sortArray[2] = ProductCatalogDao.SORT_BY_NAME_DESC;
			}
		}
		try{
			
			AbstractDaoFactory factory = AbstractDaoFactory.getFactory();
			
			/*Dao����T�u�J�e�S���̏����擾����*/
			SubCategoryDao subCategoryDao = factory.getSubCategoryDao();
			allSubCategoryList = subCategoryDao.getSubCategories();
			
			Iterator subCategoryIterator = allSubCategoryList.iterator();
			while(subCategoryIterator.hasNext()){
				/*�T�u�J�e�S����Bean���擾*/
				SubCategoryBean subCategory
				= (SubCategoryBean)subCategoryIterator.next();
				/*Bean�̒��̃T�u�J�e�S���̖��O�ƁA�N���C�A���g���I������
				  �T�u�J�e�S���̖��O�������Ȃ�A���̃T�u�J�e�S����ID��
				  �ϐ��Ɋi�[����*/
				if(subCategory.getSubCategoryName()
					.equals(selectedSubCategory)){
						selectedSubCategoryId
						= subCategory.getSubCategoryId();
					break;
				}
			}
			
			/*Dao���珤�i�̏����擾����*/
			ProductCatalogDao pcd = factory.getProductCatalogDao();
			
			/*�S���i�̏��*/
			/*sortArray�ɂ���āA�ǂ̂悤�Ƀ\�[�g�����邩�����肳���Ă���*/
			allCatalogList = pcd.getProductCatalogs(sortArray);
			
			/*Dao���珤�i�̏����擾����*/
			TagDao tagDao = factory.getTagDao();
			
			/*Tag���珤�i�̏����擾����*/
			allTagList = tagDao.getTags();
			
			/*�I�����ꂽ�T�u�J�e�S���̏��i�������������̕ϐ���錾*/
			int foundProductCount = 0;
			
			Iterator catalogIterator = allCatalogList.iterator();
			while(catalogIterator.hasNext()){
				/*Iterator���g����List���̏��i����Bean�����o��*/
				ProductCatalogBean product
					= (ProductCatalogBean)catalogIterator.next();
				
				/*�I�����ꂽ�T�u�J�e�S���̏��i�Ȃ�A�������i�����P���₷*/
				if(product.getSubCategoryId() == selectedSubCategoryId){
					foundProductCount++;
				}
				
				/*���݂̃y�[�W�ԍ��ŕ\�����ׂ����i�Ȃ�A
				  ���̏��Ƃ���ɕt������Ă���^�O�̖��O��List�Ɋi�[����*/
				if((pageNum - 1) * 15 < foundProductCount 
				&& foundProductCount <= pageNum * 15){
					
					/*���݂̏��i��񂪎��^�O�̖��O���i�[����List�̐錾*/
					List tagNames = new ArrayList();
					
					Iterator tagIterator = allTagList.iterator();
					while(tagIterator.hasNext()){
						TagBean tagRelation = (TagBean)tagIterator.next();
						/*���݂̏��i����ID���^�O�\�̍s�̏��iID�ƈ�v����Ȃ�A*/
						if(product.getExampleProductId()
						== tagRelation.getProductId()){
							/*���̃^�O�̖��O��ArrayList�ɉ�����*/
							tagNames.add(tagRelation.getTagName());
						}
					}
					
					/*���i�̏��ƁA�t������Ă���^�O�̖��O��Map�Ɋi�[����*/
					Map productData = new HashMap();
					productData.put("productCatalog",product);
					productData.put("tagNames",tagNames);
					/*�i�[����Map��List�ɒǉ�����*/
					returnProductsDataList.add(productData);
				}
			}
			
			
		}catch(IntegrationException e){
			throw new LogicException(e.getMessage(), e);
		}
		
		/*�Z�b�V�����X�R�[�v�ɃC���X�^���X��ۑ�*/
		requestContext.setSessionAttribute("pageNumber",pageNum);
		
		/*response�ő���l���Z�b�g*/
		responseContext.setResult(returnProductsDataList);
		
		/*�]����̃r���[���w��*/
		responseContext.setTarget("productlist");
		
		/*return�Ō��ʂ�Ԃ�*/
		return responseContext;
	}
}