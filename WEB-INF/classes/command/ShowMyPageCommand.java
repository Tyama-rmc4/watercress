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
 *@author 河野,宇津野
 *@date 2017/01/31
 *@description アカウント情報、お気に入り商品、おすすめ商品
 */

public class ShowMyPageCommand extends AbstractCommand{
	
	/*クライアントからのリクエスト*/
	private RequestContext requestContext;
	/*Daoからお気に入り表全件入れるList*/
	private List favoriteList;
	/*会員のお気に入り商品IDのみ入れるList*/
	private ArrayList<String> memberFavoriteList =
		new ArrayList<String>();
	/*商品全件を入れるList*/
	private List productList;
	/*会員のお気に入り商品を入れるList*/
	private ArrayList<ProductBean> memberProductList =
		new ArrayList<ProductBean>();
	
	public ResponseContext execute(ResponseContext responseContext) throws LogicException{
		/*RequestContextのインスタンスを取得*/
		RequestContext requestContext = new WebRequestContext();
		
		/*入力されたパラメータを受け取る*/
		int memberId = Integer.parseInt(requestContext.getSessionAttribute("login").toString());
		
		/*MemberBeanのインスタンス*/
		MemberBean member = new MemberBean();
		
		/*ProductImageBeanの情報が格納してある変数*/
		ProductImageBean productsImageBean = null;
		
		/*商品画像のIDを格納してある変数*/
		String imageProductId = null;
		
		/*タグのリストを全件取得するための変数*/
		List tags = null;
		
		/*オススメ商品のみ格納したリスト*/
		List tagsName = null;
		
		/*メンバーリストを全件取得のためのリスト*/
		List allMemberList = null;
		
		/*商品画像を全件取得のためのリスト*/
		List allProductsImage = null;
		
		/*最終的に返すアカウント情報のリスト*/
		List membersProfile = null;
		
		/*最終的に返すお気に入り商品のリスト*/
		List favoriteProducts = null;
		
		/*最終的に返すおすすめ商品リスト*/
		List adviceProducts = null;
		
		/*最終的に返すおすすめ商品画像リスト*/
		List adviceProductImage = null;
		
		/*最終的に返すお気に入り商品画像リスト*/
		List favoriteProductImage = null;
		try{
		/*プロフィールの取得処理ーーーーーーーーーーーーーーーーーーーー*/
			
			/*AbstractDaoFactoryのインスタンスを取得*/
			AbstractDaoFactory factory = AbstractDaoFactory.getFactory();
			
			/*MemberDaoのインスタンスを取得*/
			MemberDao memberDao = factory.getMemberDao();
			
			/*メンバーリストを全件取得*/
			allMemberList = memberDao.getMembers();
			
			Iterator iterator = allMemberList.iterator();
			
			while(iterator.hasNext()){
				
				member = (MemberBean)iterator.next();
				if(memberId == member.getMemberId()){
					break;
				}
			}
			/*プロフィール情報をListに格納*/
			membersProfile.add(member);
			
		/*お気に入り一覧の取得処理ーーーーーーーーーーーーーーーーーーー*/
			
			/*FavoriteDao型の変数ににOraFavoriteDaoインスタンスを入れる*/
			FavoriteDao favoriteDao = factory.getFavoriteDao();
			/*ProductDao型の変数ににOraProductDaoインスタンスを入れる*/
			ProductDao productDao = factory.getProductDao();
			/*お気に入り表全件を取得*/
			favoriteList = favoriteDao.getFavorites();
			iterator = favoriteList.iterator();
			
			while(iterator.hasNext()){
				/*お気に入り表の内容がループ毎に一件ずつ入る*/
				FavoriteBean fb =  (FavoriteBean)iterator.next();
				/*お気に入りを表示したい会員のIDと
				お気に入り表に登録されているIDを比べ、
				等しい会員IDと結びついている商品IDを入れる*/
				if(memberId == fb.getMemberId()){
					memberFavoriteList.add(fb.getProductId());
				}
			}
			
			/*Product表を全件取得*/
			productList = productDao.getProducts();
			iterator = productList.iterator();
			
			while(iterator.hasNext()){
				/*商品情報がループ毎に一件ずつ入る*/
				ProductBean pb =  (ProductBean)iterator.next();
				/*memberFavoriteList内にある
					複数の商品IDのどれかと合致する商品IDの商品を入れる*/
				if(memberFavoriteList.contains(pb.getProductId())){
					favoriteProducts.add(pb);
				}
			}
			
			/*おすすめ商品の表示－－－－－－－－－－－－－－－－－*/
			
			TagDao tagDao = factory.getTagDao();
			/*Tag表を全件取得*/
			tags = tagDao.getTags();
			
			Iterator tagIterator = tags.iterator();
			Iterator productsIterator = productList.iterator();
			
			while(tagIterator.hasNext()){
				TagBean tagBean = (TagBean)tagIterator.next();
				String tagName = tagBean.getTagName();
				
				if(tagName == "オススメ"){
					tagsName.add(tagBean);
				}
			}
			Iterator tagNameIterator = tagsName.iterator();
			while(tagNameIterator.hasNext()){
				TagBean tagNameBean 
				= (TagBean)tagNameIterator.next();
				/*オススメ商品のProductIdを格納*/
				String tagProductId = tagNameBean.getProductId();
				while(productsIterator.hasNext()){
					ProductBean productBean 
					= (ProductBean)productsIterator.next();
				/*商品のProductIdを格納*/
					String productId = productBean.getProductId();
					if(productId == tagProductId){
						adviceProducts.add(productBean);
					}
				}
			}
			/*おすすめ商品の商品画像の処理－－－－－－－－－－－－－*/
			
			/*ProductImageDaoのインスタンスを取得*/
			ProductImageDao productImage = factory.getProductImageDao();
			
			/*ProductsImageリストを全件取得*/
			allProductsImage = productImage.getProductImages();
			
			Iterator productsImageIterator = allProductsImage.iterator();
			Iterator adviceProductsIterator = adviceProducts.iterator();
			
			while(productsImageIterator.hasNext()){
				productsImageBean 
				= (ProductImageBean)productsImageIterator.next();
				/*ProductImageBeanのproductIdを格納*/
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
			
			/*お気に入り商品の商品画像の処理*/
			Iterator favoriteProductsIterator = favoriteProducts.iterator();
			
			while(productsImageIterator.hasNext()){
				productsImageBean 
				= (ProductImageBean)productsImageIterator.next();
				/*ProductImageBeanのproductIdを格納*/
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
		/*アカウント情報表示用のリスト*/
		requestContext.setRequestAttribute("membersProfile",membersProfile);
		
		/*お気に入り商品表示用のリスト*/
		requestContext.setRequestAttribute("favoriteProducts",favoriteProducts);
		/*おすすめ商品表示用のリスト*/
		requestContext.setRequestAttribute("adviceProducts",adviceProducts);
		
		/*おすすめ商品画像表示用のリスト*/
		requestContext.setRequestAttribute("adviceProductImage",adviceProductImage);
		
		/*おすすめ商品表示用のリスト*/
		requestContext.setRequestAttribute("favoriteProductImage",favoriteProductImage);
		
		/*転送先のビューを指定*/
		responseContext.setTarget("mypage");
		
		/*returnで結果を返す*/
		return responseContext;
	}
}