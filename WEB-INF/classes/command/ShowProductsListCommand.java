package command;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import bean.FavoriteBean;
import bean.ProductBean;
import bean.ProductCatalogBean;
import bean.SubCategoryBean;
import bean.TagBean;
import dao.AbstractDaoFactory;
import dao.FavoriteDao;
import dao.ProductCatalogDao;
import dao.ProductDao;
import dao.SubCategoryDao;
import dao.TagDao;
import ex.IntegrationException;
import ex.LogicException;
import logic.RequestContext;
import logic.ResponseContext;

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
 *@date 2017/02/13 �i�[���錋�ʂ̎d�l���A���������ɊY�����鏤�i�̐����܂ނ��̂�
                    �ύX���܂����B
 *@description
 resultData : Map<String, Object> �yjsp��${data}�Ŏ��o����镔���z
 ��
 ��"productCount",Integer ���������ɊY�����鏤�i�̐�
 ��
 ��"pageNumber",Integer �\������y�[�W�ԍ�
 ��
 ��"productData" : List<Map>
   ��productData : Map<String, Object> ���i�ꌏ���̃f�[�^
     ��
     ��"catalog",ProductCatalogBean ����Bean�̓��e�ʂ�́A���O�Ȃǂ̃f�[�^
     ��"tagNames",List<String> ���̏��i�ɕt������Ă���^�O�̖��O��List
     ��"colors",List<String> ���̏��i�̐F�̉摜�p�X��List
     ��"isFavorite",Boolean ���̏��i�̓��O�C�����̉���̂��C�ɓ���ł��邩
 */

public class ShowProductsListCommand extends AbstractCommand{

	/*Dao���擾���邽�߂�DaoFactory���i�[����*/
	AbstractDaoFactory factory;

	public ResponseContext execute(ResponseContext responseContext)
	throws LogicException{
		/*RequestContext�̃C���X�^���X���擾*/
		RequestContext requestContext = getRequestContext();

		/*���M����Ă����e�p�����[�^�̎擾*/
		/*�y�[�W�̔ԍ��B�p�����[�^�������ꍇ��1�y�[�W�ڂ�\������*/
		Integer pageNumber = 1;
		String[] pageNumberParam
		= requestContext.getParameter("pageNumber");
		if(pageNumberParam != null){
			pageNumber = Integer.parseInt(pageNumberParam[0]);
		}
		/*�I�����ꂽ�T�u�J�e�S���̖��O*/
		String selectedSubCategoryName = null;
		String[] selectedSubCategoryParam
		= requestContext.getParameter("subCategory");
		if(selectedSubCategoryParam != null){
			selectedSubCategoryName = selectedSubCategoryParam[0];
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


		/*���̃R�}���h��result�ƂȂ�A�e���i�̏��ƊY�����鏤�i�̐�*/
		Map resultData = new HashMap();

		/*��L��Map�Ɋ܂܂��A
		  �I�����ꂽ�T�u�J�e�S���̏��i�������������̕ϐ�*/
		Integer foundProductCount = 0;

		/*��L��Map�Ɋ܂܂��A���i���Ƃ��̃^�O���̃��X�g*/
		List<Map> productsDataList = new ArrayList<Map>();

		/*�S���i�̏��̃��X�g*/
		List allCatalogList = null;
		/*�S�^�O�̏��̃��X�g*/
		List allTagList = null;

		try{
			/*Dao���擾���邽�߂�DaoFactory���擾����*/
			factory = AbstractDaoFactory.getFactory();

			/*�I�����ꂽ�T�u�J�e�S����ID�𕪊��������\�b�h�Ŏ擾����*/
			int selectedSubCategoryId
			= getSubCategoryId(selectedSubCategoryName);

			/*���݃��O�C�����̉���́A
			  ���C�ɓ���̏��i��ID�̃��X�g�𕪊��������\�b�h�Ŏ擾����*/
			List memberFavoriteList = getMemberFavoriteList(loginMemberId);

			/*���i�̏����擾���邽�߂�Dao���擾����*/
			ProductCatalogDao pcd = factory.getProductCatalogDao();
			/*Dao���珤�i�̏����擾����BsortArray�ɂ���ă\�[�g������ݒ�*/
			allCatalogList = pcd.getProductCatalogs(sortArray);

			/*�^�O�̏����擾���邽�߂�Dao���擾����*/
			TagDao tagDao = factory.getTagDao();
			/*Dao����^�O�̏����擾����*/
			allTagList = tagDao.getTags();

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
							productData.put("catalog",product);
							productData.put("tagNames",productTagNames);
							/*���i�̐F��\���摜�̃p�X��
							  �����������\�b�h����擾����*/
							List<String> productColors
							= getProductColors(product.getProductName());
							productData.put("colors",productColors);
							/*���̏��i�̓��O�C�����̉���̂��C�ɓ���
							  �ł��邩���m�F����*/
							Boolean isFavorite = new Boolean(false);
							if(memberFavoriteList.contains(
								product.getExampleProductId())){
								isFavorite = true;
							}
							productData.put("isFavorite",isFavorite);
							/*�i�[����Map��List�ɒǉ�����*/
							productsDataList.add(productData);
						}/*if(���݂̃y�[�W�ŕ\�����ׂ���) �̏I�[*/
					}/*if(����������A�����^�O�ɍ��v���邩)*/
				}/*if(�I�������T�u�J�e�S����) �̏I�[*/
			}/*while(catalogIterator.hasNext()) �̏I�[*/

		}catch (IntegrationException e){
			throw new LogicException(e.getMessage(), e);
		}

		/*result�ł���Map�Ƀf�[�^���i�[����*/
		/*���i�̌��BforEach��p���邽�ߓ��e�̖����z��ɂ���*/
		resultData.put("productCount", new int[foundProductCount]);
		/*�y�[�W�ԍ�*/
		resultData.put("pageNumber", pageNumber);
		/*�e���i�̃f�[�^*/
		resultData.put("productsData", productsDataList);

		/*response�ő���l���Z�b�g*/
		responseContext.setResult(resultData);

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

	/*�I�����ꂽ�T�u�J�e�S���̖��O�ɑΉ�����ID��Ԃ����\�b�h*/
	private int getSubCategoryId(String subCategoryName)
	throws LogicException{
		/*�S�T�u�J�e�S���̏��̃��X�g*/
		List allSubCategoryList = null;
		try{
			/*�T�u�J�e�S���̏����擾���邽�߂�Dao���擾����*/
			SubCategoryDao subCategoryDao = factory.getSubCategoryDao();
			/*Dao����T�u�J�e�S���̏����擾����*/
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
						.equals(subCategoryName)){
					return subCategory.getSubCategoryId();
				}
			}
		}catch (IntegrationException e){
			throw new LogicException(e.getMessage(), e);
		}
		return -1;
	}

	/*���݃��O�C�����̉���́A
	  ���C�ɓ���̏��i��ID�̃��X�g�𕪊��������\�b�h�Ŏ擾���郁�\�b�h*/
	private List getMemberFavoriteList(int loginMemberId)
	throws LogicException{
		/*�S���C�ɓ���̏��̃��X�g*/
		List allFavoriteList = null;
		/*���̃��\�b�h���Ԃ��A
		  ���O�C�����̉���̂��C�ɓ��菤�i��ID�̃��X�g*/
		List memberFavoriteList = new ArrayList();
		try{
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
					/*���C�ɓ���̏��̉��ID�ƃ��O�C�����̉����ID��
					  �����Ȃ炻�̂��C�ɓ�����̏��i��ID�����X�g�ɒǉ�*/
					if(loginMemberId == favorite.getMemberId()){
						memberFavoriteList.add(favorite.getProductId());
					}
				}
			}
		}catch (IntegrationException e){
			throw new LogicException(e.getMessage(), e);
		}
		return memberFavoriteList;
	}


	/*�����̏��i���Ɩ��O����v���鏤�i�́A�F�̉摜�p�X�̃��X�g��Ԃ����\�b�h*/
	private List<String> getProductColors(String productName)
	throws LogicException{
		/*���̃��\�b�h���Ԃ��A�F�̉摜�p�X�̃��X�g�̕ϐ���錾*/
		List<String> productColors = new ArrayList<String>();

		/*�v���p�e�B�t�@�C���ւ̃p�X */
		String FILE_PATH
		= "c:/watercress/WEB-INF/data/properties/ProductColors.properties";

		/*�F���̉摜�p�X��ۑ����Ă���v���p�e�B�t�@�C����ǂݍ���*/
		Properties properties = new Properties();

		try{
			properties.load(new FileInputStream(FILE_PATH));

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