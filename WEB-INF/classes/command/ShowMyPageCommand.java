package command;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import bean.MemberBean;
import bean.ProductBean;
import bean.FavoriteBean;
import bean.TagBean;
import bean.ProductImageBean;

import dao.AbstractDaoFactory;
import dao.MemberDao;
import dao.ProductDao;
import dao.FavoriteDao;
import dao.TagDao;
import dao.ProductImageDao;


import logic.ResponseContext;
import logic.RequestContext;
import logic.WebRequestContext;
import ex.IntegrationException;
import ex.LogicException;

/**
 *@className UserStatusCommand
 *@author �͖�,�F�Ö�
 *@date 2017/01/31
 *@description �A�J�E���g���A���C�ɓ��菤�i�A�������ߏ��i
 */

public class ShowMyPageCommand extends AbstractCommand{
	
	/*�N���C�A���g����̃��N�G�X�g*/
	private RequestContext requestContext;
	/*Dao���炨�C�ɓ���\�S�������List*/
	private List favoriteList;
	/*����̂��C�ɓ��菤�iID�̂ݓ����List*/
	private ArrayList<String> memberFavoriteList =
		new ArrayList<String>();
	/*���i�S��������List*/
	private List productList;
	/*����̂��C�ɓ��菤�i������List*/
	private ArrayList<ProductBean> memberProductList =
		new ArrayList<ProductBean>();
	
	public ResponseContext execute(ResponseContext responseContext) throws LogicException{
		/*RequestContext�̃C���X�^���X���擾*/
		RequestContext requestContext = new WebRequestContext();
		
		/*���͂��ꂽ�p�����[�^���󂯎��*/
		int memberId = Integer.parseInt(requestContext.getSessionAttribute("login").toString());
		
		/*MemberBean�̃C���X�^���X*/
		MemberBean member = new MemberBean();
		
		/*ProductImageBean�̏�񂪊i�[���Ă���ϐ�*/
		ProductImageBean productsImageBean = null;
		
		/*���i�摜��ID���i�[���Ă���ϐ�*/
		String imageProductId = null;
		
		/*�^�O�̃��X�g��S���擾���邽�߂̕ϐ�*/
		List tags = null;
		
		/*�I�X�X�����i�̂݊i�[�������X�g*/
		List tagsName = null;
		
		/*�����o�[���X�g��S���擾�̂��߂̃��X�g*/
		List allMemberList = null;
		
		/*���i�摜��S���擾�̂��߂̃��X�g*/
		List allProductsImage = null;
		
		/*�ŏI�I�ɕԂ��A�J�E���g���̃��X�g*/
		List membersProfile = null;
		
		/*�ŏI�I�ɕԂ����C�ɓ��菤�i�̃��X�g*/
		List favoriteProducts = null;
		
		/*�ŏI�I�ɕԂ��������ߏ��i���X�g*/
		List adviceProducts = null;
		
		/*�ŏI�I�ɕԂ��������ߏ��i�摜���X�g*/
		List adviceProductImage = null;
		
		/*�ŏI�I�ɕԂ����C�ɓ��菤�i�摜���X�g*/
		List favoriteProductImage = null;
		try{
		/*�v���t�B�[���̎擾�����[�[�[�[�[�[�[�[�[�[�[�[�[�[�[�[�[�[�[�[*/
			
			/*AbstractDaoFactory�̃C���X�^���X���擾*/
			AbstractDaoFactory factory = AbstractDaoFactory.getFactory();
			
			/*MemberDao�̃C���X�^���X���擾*/
			MemberDao memberDao = factory.getMemberDao();
			
			/*�����o�[���X�g��S���擾*/
			allMemberList = memberDao.getMembers();
			
			Iterator iterator = allMemberList.iterator();
			
			while(iterator.hasNext()){
				
				member = (MemberBean)iterator.next();
				if(memberId == member.getMemberId()){
					break;
				}
			}
			/*�v���t�B�[������List�Ɋi�[*/
			membersProfile.add(member);
			
		/*���C�ɓ���ꗗ�̎擾�����[�[�[�[�[�[�[�[�[�[�[�[�[�[�[�[�[�[�[*/
			
			/*FavoriteDao�^�̕ϐ��ɂ�OraFavoriteDao�C���X�^���X������*/
			FavoriteDao favoriteDao = factory.getFavoriteDao();
			/*ProductDao�^�̕ϐ��ɂ�OraProductDao�C���X�^���X������*/
			ProductDao productDao = factory.getProductDao();
			/*���C�ɓ���\�S�����擾*/
			favoriteList = favoriteDao.getFavorites();
			iterator = favoriteList.iterator();
			
			while(iterator.hasNext()){
				/*���C�ɓ���\�̓��e�����[�v���Ɉꌏ������*/
				FavoriteBean fb =  (FavoriteBean)iterator.next();
				/*���C�ɓ����\�������������ID��
				���C�ɓ���\�ɓo�^����Ă���ID���ׁA
				���������ID�ƌ��т��Ă��鏤�iID������*/
				if(memberId == fb.getMemberId()){
					memberFavoriteList.add(fb.getProductId());
				}
			}
			
			/*Product�\��S���擾*/
			productList = productDao.getProducts();
			iterator = productList.iterator();
			
			while(iterator.hasNext()){
				/*���i��񂪃��[�v���Ɉꌏ������*/
				ProductBean pb =  (ProductBean)iterator.next();
				/*memberFavoriteList���ɂ���
					�����̏��iID�̂ǂꂩ�ƍ��v���鏤�iID�̏��i������*/
				if(memberFavoriteList.contains(pb.getProductId())){
					favoriteProducts.add(pb);
				}
			}
			
			/*�������ߏ��i�̕\���|�|�|�|�|�|�|�|�|�|�|�|�|�|�|�|�|*/
			
			TagDao tagDao = factory.getTagDao();
			/*Tag�\��S���擾*/
			tags = tagDao.getTags();
			
			Iterator tagIterator = tags.iterator();
			Iterator productsIterator = productList.iterator();
			
			while(tagIterator.hasNext()){
				TagBean tagBean = (TagBean)tagIterator.next();
				String tagName = tagBean.getTagName();
				
				if(tagName == "�I�X�X��"){
					tagsName.add(tagBean);
				}
			}
			Iterator tagNameIterator = tagsName.iterator();
			while(tagNameIterator.hasNext()){
				TagBean tagNameBean 
				= (TagBean)tagNameIterator.next();
				/*�I�X�X�����i��ProductId���i�[*/
				String tagProductId = tagNameBean.getProductId();
				while(productsIterator.hasNext()){
					ProductBean productBean 
					= (ProductBean)productsIterator.next();
				/*���i��ProductId���i�[*/
					String productId = productBean.getProductId();
					if(productId == tagProductId){
						adviceProducts.add(productBean);
					}
				}
			}
			/*�������ߏ��i�̏��i�摜�̏����|�|�|�|�|�|�|�|�|�|�|�|�|*/
			
			/*ProductImageDao�̃C���X�^���X���擾*/
			ProductImageDao productImage = factory.getProductImageDao();
			
			/*ProductsImage���X�g��S���擾*/
			allProductsImage = productImage.getProductImages();
			
			Iterator productsImageIterator = allProductsImage.iterator();
			Iterator adviceProductsIterator = adviceProducts.iterator();
			
			while(productsImageIterator.hasNext()){
				productsImageBean 
				= (ProductImageBean)productsImageIterator.next();
				/*ProductImageBean��productId���i�[*/
				imageProductId = productsImageBean.getProductId();
				
				while(adviceProductsIterator.hasNext()){
					String adviceProductId 
					= ((ProductBean)adviceProductsIterator.next())
					.getProductId();
					if(imageProductId == adviceProductId){
						adviceProductImage.add(productsImageBean);
					}
				}
			}
			
			/*���C�ɓ��菤�i�̏��i�摜�̏���*/
			Iterator favoriteProductsIterator = favoriteProducts.iterator();
			
			while(productsImageIterator.hasNext()){
				productsImageBean 
				= (ProductImageBean)productsImageIterator.next();
				/*ProductImageBean��productId���i�[*/
				imageProductId = productsImageBean.getProductId();
				
				while(favoriteProductsIterator.hasNext()){
					String favoriteProductId 
					= ((ProductBean)favoriteProductsIterator.next())
					.getProductId();
					if(favoriteProductId == imageProductId){
						favoriteProductImage.add(productsImageBean);
					}
				}
			}
		}catch(IntegrationException e){
			throw new LogicException(e.getMessage(), e);
		}
		/*�A�J�E���g���\���p�̃��X�g*/
		requestContext.setRequestAttribute("membersProfile",membersProfile);
		
		/*���C�ɓ��菤�i�\���p�̃��X�g*/
		requestContext.setRequestAttribute("favoriteProducts",favoriteProducts);
		/*�������ߏ��i�\���p�̃��X�g*/
		requestContext.setRequestAttribute("adviceProducts",adviceProducts);
		
		/*�������ߏ��i�摜�\���p�̃��X�g*/
		requestContext.setRequestAttribute("adviceProductImage",adviceProductImage);
		
		/*�������ߏ��i�\���p�̃��X�g*/
		requestContext.setRequestAttribute("favoriteProductImage",favoriteProductImage);
		
		/*�]����̃r���[���w��*/
		responseContext.setTarget("mypage");
		
		/*return�Ō��ʂ�Ԃ�*/
		return responseContext;
	}
}