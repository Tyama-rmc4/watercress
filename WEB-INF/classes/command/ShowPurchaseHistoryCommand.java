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
 *@author 河野
 *@date 2017/02/08
 *@description 購入履歴を表示
 */

public class ShowPurchaseHistoryCommand extends AbstractCommand{
	
	public ResponseContext execute(ResponseContext responseContext)
	throws LogicException{
		/*RequestContextのインスタンスを取得*/
		RequestContext requestContext = new WebRequestContext();
		
		/*getParameterでページ番号を取得*/
		int pageNum = 0;
		String cushionPage = requestContext.getParameter("pageNumber")[0];
		if(cushionPage != null){
			pageNum = Integer.parseInt(requestContext.getParameter("pageNumber")[0]);
		}
		
		
		/*memberIdを受け取る*/
		int memberId = Integer.parseInt(requestContext.getSessionAttribute("login").toString()) ;
		
		/*カウント用の変数*/
		int i=0;
		
		/*returnで返すための購入リスト*/
		List memberPurchaseHistory = null;
		
		/*returnで返すためのProductリスト*/
		List memberProducts = null;
		
		/*returnで返すためのProductsImageリスト*/
		List memberProductsImage = null;
		
		/*Product表のリストを全件取得のためのリスト*/
		List allProducts = null;
		
		/*購入リストを全件取得のためのリスト*/
		List allPurchaseProducts = null;
		
		/*ProductImageリストを全件取得のためのリスト*/
		List allProductsImage = null;
		
		try{
			/*AbstractDaoFactoryのインスタンスを取得*/
			AbstractDaoFactory factory = AbstractDaoFactory.getFactory();
			
			/*ProductDaoのインスタンスを取得*/
			ProductDao productDao = factory.getProductDao();
			
			/*PurchaseHistoryDaoのインスタンスを取得*/
			PurchaseHistoryDao purchaseHistoryDao 
			= factory.getPurchaseHistoryDao();
			
			/*ProductImageDaoのインスタンスを取得*/
			ProductImageDao productImage = factory.getProductImageDao();
			
			/*Productsリストを全件取得*/
			allProducts = productDao.getProducts();
			
			/*購入リストを全件取得*/
			allPurchaseProducts = purchaseHistoryDao.getPurchaseHistories();
			
			/*ProductsImageリストを全件取得*/
			allProductsImage = productImage.getProductImages();
			
			Iterator allPurchaseIterator =  allPurchaseProducts.iterator();
			
			while(allPurchaseIterator.hasNext()){
				
				// purchasehistorybeanに入れる
				PurchaseHistoryBean memberHistoryBean 
				= (PurchaseHistoryBean)allPurchaseIterator.next();
				
				if((pageNum - 1) * 10 <= i && i < pageNum * 10){
					/*memberIdに一致するメンバーで購入リストを絞る*/
					if(memberId == memberHistoryBean.getMemberId()){
						memberPurchaseHistory.add(memberHistoryBean);
						
					}
				}
				i++;
			}
			
			/*全商品リストのイテレータ*/
			Iterator allProductsIterator = allProducts.iterator();
			/*10件分の購入リストのイテレータ*/
			Iterator historiesIterator = memberPurchaseHistory.iterator();
			/*全商品Imageリストのイテレータ*/
			Iterator productsImageIterator = allProductsImage.iterator();
			while(allProductsIterator.hasNext()){
				
				/*Productリストを一件ずつ格納*/
				ProductBean productBean = (ProductBean)allProductsIterator.next();
				
				/*商品のIDをproductIdの変数に格納*/
				String productId = productBean.getProductId();
				
				while(historiesIterator.hasNext()){
					/*購入リストからすべての商品IDを格納*/
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
				
				/*ProductImageリストを一件ずつ格納*/
					ProductImageBean productsImageBean 
					= (ProductImageBean)productsImageIterator.next();
				
				/*商品のIDをproductImageIdの変数に格納*/
					String productImageId = productsImageBean.getProductId();
				
				while(historiesIterator.hasNext()){
					/*購入リストからすべての商品IDを格納*/
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
		
		/*リクエストスコープにインスタンスを保存*/
		requestContext.setRequestAttribute("products",memberProducts);
		requestContext.setRequestAttribute("purchaseProducts",memberPurchaseHistory);
		requestContext.setRequestAttribute("productsImage",memberProductsImage);
		/*セッションスコープにインスタンスを保存*/
		requestContext.setSessionAttribute("pageNum",pageNum);
		
		/*転送先のビューを指定*/
		responseContext.setTarget("orderhistory");
		
		/*returnで結果を返す*/
		return responseContext;
	}
}