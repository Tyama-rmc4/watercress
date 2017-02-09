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
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import bean.TagBean;
import dao.TagDao;
import bean.ProductBean;
import dao.ProductDao;
import bean.FavoriteBean;
import dao.FavoriteDao;

/**
 *@className ShowProductsListCommand
 *@author �͖�
 *@date 2017/01/31 �y�[�W�ԍ����Z�b�V�����ŊǗ�����C�������܂����B
 *@author �r�c
 *@date 2017/02/07 �i�[���錋�ʂ̎d�l���A�^�O�̖��O���܂ނ��̂ɕύX���܂����B
 *@date 2017/02/08 ���i���A�^�O���ł̌������s�����Ƃ��ł���悤�ɂ��܂����B
 *@date 2017/02/09 ���ʂ�productData�ɁA���i�̐F��\���摜�̃p�X��List�ł���
                   "productColors"��ǉ����܂����B
                   ���ʂ�productData�ɁA���̏��i�̓��O�C�����̉���̂��C�ɓ���
                   �ł��邩��\��Boolean�ł���"isFavoirte"��ǉ����܂����B
 *@description 
 data : List<Map> �yjsp��${data}�Ŏ��o����镔���z
 ��
 ��productData : Map<String, Object>
   ��
   ��"productCatalog",ProductCatalogBean
   ��"productTagNames",List<String> ���̏��i�ɕt������Ă���^�O�̖��O��List
   ��"productColors",List<String> ���̏��i�̐F�̉摜�p�X��List
   ��"isFavorite",Boolean ���̏��i�̓��O�C�����̉���̂��C�ɓ���ł��邩
 */

public class ShowProductsListCommand extends AbstractCommand{
	
	public ResponseContext execute(ResponseContext responseContext)
	throws LogicException{
		/*RequestContext�̃C���X�^���X���擾*/
		RequestContext requestContext = getRequestContext();
		
		/*���M����Ă����e�p�����[�^�̎擾*/
		/*�y�[�W�̔ԍ��B�p�����[�^�������ꍇ��1�y�[�W�ڂ�\������*/
		int pageNumber = 1;
		String[] pageNumberParam
		= requestContext.getParameter("pageNumber");
		if(pageNumberParam != null){
			pageNumber = Integer.parseInt(pageNumberParam[0]);
		}
		/*�I�����ꂽ�T�u�J�e�S���̖��O*/
		String selectedSubCategory = null;
		String[] selectedSubCategoryParam
		= requestContext.getParameter("subCategory");
		if(selectedSubCategoryParam != null){
			selectedSubCategory = selectedSubCategoryParam[0];
		}
		/*�������閼�O  �S�p�X�y�[�X�𔼊p�X�y�[�X�ɕϊ�������A
		  ���p�X�y�[�X�ŕ������Ĕz��ɂ���*/
		String[] searchTexts = null;
		String[] searchTextParam
		= requestContext.getParameter("searchText");
		if(searchTextParam != null){
			String searchTextString
			= searchTextParam[0].replaceAll("�@"," ");
			searchTexts = searchTextString.split(" ");
		}
		/*��������^�O  �S�p�X�y�[�X�𔼊p�X�y�[�X�ɕϊ�������A
		  ���p�X�y�[�X�ŕ������Ĕz��ɂ��AList�ɂ���*/
		String[] searchTagParam = requestContext.getParameter("searchTag");
		String[] searchTagArray = null;
		List searchTags = null;
		if(searchTagParam != null){
			String searchTagString = searchTagParam[0].replaceAll("�@"," ");
			searchTagArray = searchTagString.split(" ");
			searchTags = new ArrayList(Arrays.asList(searchTagArray));
		}
		/*�\�[�g����*/
		String[] sortParams = requestContext.getParameter("sort");
		/*getProductCatalogs�ɓn���A�\�[�g�����̈������쐬����*/
		/*sortParams�̕�������AcreateSortArray���\�b�h�̃��[����
		  �]����int�z��ɕϊ�����B��������i�����̍ۂɈ����ɂ���*/
		int[] sortArray = createSortArray(sortParams);
		
		/*���O�C�����̉����ID���擾����B*/
		/*���O�C�����Ă��Ȃ��ꍇ��-1���i�[����B*/
		int loginMemberId = -1;
		if(requestContext.getSessionAttribute("login") != null){
			String idAttribute
			= (String)requestContext.getSessionAttribute("login");
			loginMemberId = Integer.parseInt(idAttribute);
		}
		
		/*�S�T�u�J�e�S���̏��̃��X�g*/
		List allSubCategoryList = null; 
		/*�S���i�̏��̃��X�g*/
		List allCatalogList = null; 
		/*�S�^�O�̏��̃��X�g*/
		List allTagList = null;
		/*�S���C�ɓ���̏��̃��X�g*/
		List allFavoriteList = null;
		
		/*���̃R�}���h��Result�ƂȂ�A���i���Ƃ��̃^�O���̃��X�g*/
		List<Map> returnProductsDataList = new ArrayList<Map>();
		
		/*�I�����ꂽ�T�u�J�e�S����ID���i�[����ϐ���錾*/
		int selectedSubCategoryId = 0;
		try{
			/*Dao���擾���邽�߂�DaoFactory���擾*/
			AbstractDaoFactory factory = AbstractDaoFactory.getFactory();
			
			/*Dao����T�u�J�e�S���̏����擾����*/
			SubCategoryDao subCategoryDao = factory.getSubCategoryDao();
			allSubCategoryList = subCategoryDao.getSubCategories();
			
			/*Iterator���g���A�e�T�u�J�e�S���̏����m�F*/
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
			
			/*���i�̏����擾���邽�߂�Dao���擾����*/
			ProductCatalogDao pcd = factory.getProductCatalogDao();
			/*Dao���珤�i�̏����擾����BsortArray�ɂ���ă\�[�g������ݒ�*/
			allCatalogList = pcd.getProductCatalogs(sortArray);
			
			/*�^�O�̏����擾���邽�߂�Dao���擾����*/
			TagDao tagDao = factory.getTagDao();
			/*Dao����^�O�̏����擾����*/
			allTagList = tagDao.getTags();
			
			/*���݃��O�C�����̉���́A���C�ɓ���̏��i��ID�̃��X�g��錾*/
			List memberFavoriteList = new ArrayList();
			/*��������O�C�����Ă���Ȃ�A���C�ɓ���̏��i��ID���擾����*/
			if(loginMemberId != -1){
				/*���C�ɓ���̏����擾���邽�߂�Dao���擾����*/
				FavoriteDao favoriteDao = factory.getFavoriteDao();
				/*Dao���炨�C�ɓ���̏����擾����*/
				allFavoriteList = favoriteDao.getFavorites();
				
				Iterator favoriteIterator = allFavoriteList.iterator();
				while(favoriteIterator.hasNext()){
					FavoriteBean favorite
					= (FavoriteBean)favoriteIterator.next();
					if(loginMemberId == favorite.getMemberId()){
						memberFavoriteList.add(favorite.getProductId());
					}
				}
			}
			
			/*�I�����ꂽ�T�u�J�e�S���̏��i�������������̕ϐ���錾*/
			int foundProductCount = 0;
			
			Iterator catalogIterator = allCatalogList.iterator();
			while(catalogIterator.hasNext()){
				/*Iterator���g����List���̏��i����Bean�����o��*/
				ProductCatalogBean product
					= (ProductCatalogBean)catalogIterator.next();
				
				/*�I�������T�u�J�e�S���̏��i�Ȃ�A���̌������v����ɐi��*/
				if(product.getSubCategoryId() == selectedSubCategoryId){
					
					/*���̏��i�ɕt������Ă���^�O�����̎��_�Ŏ擾���Ă���*/
					/*���݂̏��i���̃^�O�̖��O���i�[����List�̐錾*/
					List<String> productTagNames = new ArrayList<String>();
						
					Iterator tagIterator = allTagList.iterator();
					while(tagIterator.hasNext()){
						TagBean tagRelation = (TagBean)tagIterator.next();
						/*���݂̏��i����ID��
						  �^�O�\�̍s�̏��iID�ƈ�v����Ȃ�A*/
						if(product.getExampleProductId()
						== tagRelation.getProductId()){
							/*���̃^�O�̖��O��ArrayList�ɉ�����*/
							productTagNames.add(tagRelation.getTagName());
						}
					}
					
					/*���������̕�����ɍ��v���邩�����ϐ��̐錾*/
					boolean isMatchText = false;
					/*���������̃^�O�ɍ��v���邩�����ϐ��̐錾*/
					boolean isMatchTag = false;
					
					/*���������̕����񂪑��݂��Ȃ��Ȃ��ɏ������v�Ƃ���*/
					if(searchTexts == null){
						isMatchText = true;
					}else{
						/*���������̕����񂪑��݂���ꍇ�A
						  ����������S�Ăƍ��v���邩�𔻒肷��*/
						/*�u�S�Ă̌���������������Ă���v�����ϐ���錾*/
						boolean isAllTextExist = true;
						for(String searchText : searchTexts){
							/*���i����������������܂�ł��Ȃ��Ȃ�*/
							if(product.getProductName().indexOf(searchText)
								== -1){
								/*�u�S�Ă̌���������������Ă���v���U�ɂ���*/
								isAllTextExist = false;
							}
						}
						/*�u�S�Ă̌���������������Ă���v���^�Ȃ�*/
						if(isAllTextExist){
							/*�u������̌��������ɍ��v����v���Ƃ�ϐ��Ɋi�[*/
							isMatchText = true;
						}
					}
					/*���������̃^�O�����݂��Ȃ��Ȃ��ɏ������v�Ƃ���*/
					if(searchTags == null){
						isMatchTag = true;
					}else{
						/*���������̃^�O�����݂���ꍇ�A
						  �����^�O�S�Ăƍ��v���邩�𔻒肷��*/
						/*�u�S�Ă̌����^�O�������Ă���v�����ϐ���錾*/
						boolean isAllTagExist = true;
						Iterator searchTagIterator = searchTags.iterator();
						while(searchTagIterator.hasNext()){
							String searchTagName
							= (String)searchTagIterator.next();
							/*���i�ɕt������Ă���^�O�̒���
							  ���������̃^�O�������Ȃ�*/
							if(!(productTagNames.contains(searchTagName)) ){
								/*�u�S�Ă̌����^�O�������Ă���v���U�ɂ���*/
								isMatchText = false;
							}
						}
						/*�u�S�Ă̌����^�O�������Ă���v���^�Ȃ�*/
						if(isAllTagExist){
							/*�u������̃^�O�����ɍ��v����v���Ƃ�ϐ��Ɋi�[*/
							isMatchTag = true;
						}
					}
					/*���������̕�����ƃ^�O�ɍ��v����Ȃ�A���i�����㏈����*/
					if(isMatchText && isMatchTag){
						/*�������i�����P���₷*/
						foundProductCount++;
						
						/*���݂̃y�[�W�ł��̏��i��\�����ׂ����𔻒肷��*/
						/*���݂̃y�[�W�ԍ��ŕ\�����ׂ����i�Ȃ�A���̏���
						  ����ɕt������Ă���^�O�̖��O��
						  ���̏��i�̐F��\���摜�̃p�X��
						  ���O�C�����̉���̂��C�ɓ���ł��邩��
						  Map�Ɋi�[���A�����List�Ɋi�[����*/
						if((pageNumber - 1) * 15 < foundProductCount 
						&& foundProductCount <= pageNumber * 15){
							
							/*���i�̏��ƁA�t������Ă���^�O�̖��O��
							  ���i�̐F��\���摜�̃p�X��
							  ���O�C�����̉���̂��C�ɓ���ł��邩��
							  Map�Ɋi�[����B*/
							Map productData = new HashMap();
							productData.put("productCatalog",product);
							productData.put("productTagNames",productTagNames);
							/*���i�̐F��\���摜�̃p�X��
							  �����������\�b�h����擾����*/
							List<String> productColors
							= getProductColors(product.getProductName());
							productData.put("productColors",productColors);
							/*���̏��i�̓��O�C�����̉���̂��C�ɓ���
							  �ł��邩���m�F����*/
							Boolean isFavorite = new Boolean(false);
							if(memberFavoriteList.contains(
								product.getExampleProductId())){
								isFavorite = true;
							}
							productData.put("isFavorite",isFavorite);
							/*�i�[����Map��List�ɒǉ�����*/
							returnProductsDataList.add(productData);
						}/*if(���݂̃y�[�W�ŕ\�����ׂ���) �̏I�[*/
					}/*if(����������A�����^�O�ɍ��v���邩)*/
				}/*if(�I�������T�u�J�e�S����) �̏I�[*/
			}/*while(catalogIterator.hasNext()) �̏I�[*/
			
		}catch (IntegrationException e){
			throw new LogicException(e.getMessage(), e);
		}
		
		/*�Z�b�V�����X�R�[�v�ɕ\������y�[�W�ԍ���ۑ�*/
		requestContext.setSessionAttribute("pageNumber",pageNumber);
		
		/*response�ő���l���Z�b�g*/
		responseContext.setResult(returnProductsDataList);
		
		/*�]����̃r���[���w��*/
		responseContext.setTarget("productlist");
		
		/*return�Ō��ʂ�Ԃ�*/
		return responseContext;
	}
	
	private int[] createSortArray(String[] sortParams){
		/*�ԋp����\�[�g���@�w��p�z���錾*/
		int[] sortArray = new int[3];
		
		if(sortParams != null){
			/*sortParams�̓��e�ɏ]���āA�萔��z��Ɋi�[����*/
			for(int i = 0;i<=sortParams.length;i++){
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
		}
		/*�쐬�����z���Ԃ�*/
		return sortArray;
	}
	
	/*�����̏��i���Ɩ��O����v���鏤�i�́A�F�̉摜�p�X�̃��X�g��Ԃ����\�b�h*/
	private List<String> getProductColors(String productName)
	throws LogicException{
		/*���̃��\�b�h���Ԃ��A�F�̉摜�p�X�̃��X�g�̕ϐ���錾*/
		List<String> productColors = new ArrayList<String>();
		
		/*�v���p�e�B�t�@�C���ւ̃p�X */
		String FILE_PATH = "c:/watercress/ProductColors.properties";
		
		/*�F���̉摜�p�X��ۑ����Ă���v���p�e�B�t�@�C����ǂݍ���*/
		Properties properties = new Properties();
		
		try{
			properties.load(new FileInputStream(FILE_PATH));
			
			/*Dao���擾���邽�߂�DaoFactory���擾*/
			AbstractDaoFactory factory = AbstractDaoFactory.getFactory();
			/*���i�����擾���邽�߂�ProductDao���擾*/
			ProductDao productDao = factory.getProductDao();
			
			/*�S���i�̏����擾*/
			List allProductList = productDao.getProducts();
			
			Iterator productIterator = allProductList.iterator();
			while(productIterator.hasNext()){
				
				/*���i�̏��P����Bean���擾*/
				ProductBean product = (ProductBean)productIterator.next();
				
				/*�����̏��i���ƌ��݂̏��i�̖��O�ƈ�v����Ȃ�*/
				if(productName.equals(product.getProductName())){
					/*���݂̏��i�̐F�ɑΉ�����摜�p�X���擾����*/
					String productColor
					= properties.getProperty(product.getProductColor());
					productColors.add(productColor);
				}
			}
		}catch (FileNotFoundException e) {
			throw new LogicException(e.getMessage(), e);
		}catch (IOException e) {
			throw new LogicException(e.getMessage(), e);
		}catch (IntegrationException e){
			throw new LogicException(e.getMessage(), e);
		}catch (Exception e) {
			throw new LogicException(e.getMessage(), e);
		}
		return productColors;
	}
}