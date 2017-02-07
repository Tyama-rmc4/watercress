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
 *@author 河野
 *@date 2017/01/31 ページ番号をセッションで管理する修正をしました。
 *@author 池田
 *@date 2017/02/07 格納する結果の仕様を、タグの名前を含むものに変更しました。
 *@description 
 data : List<Map> 【jspで${data}で取り出される部分】
 ┃
 ┗productData : Map<String, Object>
   ┃
   ┗"productCatalog",ProductCatalogBean
   ┗"tagNames",List<String>
 */

public class ShowProductsListCommand extends AbstractCommand{
	
	public ResponseContext execute(ResponseContext responseContext) throws LogicException{
		
		/*RequestContextのインスタンスを取得*/
		RequestContext requestContext = new WebRequestContext();
		
		/*ページの番号*/
		int pageNum = Integer.parseInt(requestContext.getParameter("pageNumber")[0]);
		
		/*選択されたサブカテゴリ*/
		String selectedSubCategory = requestContext.getParameter("subCategory")[0];
		
		int selectedSubCategoryId = 0;
		
		/*ソート用の変数*/
		String[] sortParams = requestContext.getParameter("sort");
		
		/*getProductCatalogsに渡す引数*/
		int[] sortArray = new int[3];
		
		/*全サブカテゴリの情報のリスト*/
		List allSubCategoryList = null; 
		
		/*全商品の情報のリスト*/
		List allCatalogList = null; 
		
		/*全タグの情報のリスト*/
		List allTagList = null;
		
		/*返される商品情報とそのタグ名のリスト*/
		List<Map> returnProductsDataList = new ArrayList<Map>();
		
		for(int i = 0;i<=sortParams.length;i++){
			
			/*sortParamsの中身に対応した文字列によりソートする*/
			/*
			  priceAsc = 値段の安い順
			  priceDesc = 値段の高い順
			  purchaseAsc = 購入数が少ない順
			  purchaseDesc = 購入数が多い順
			  nameAsc = 50音順
			  nameDesc = 50音順の逆順 
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
			
			/*Daoからサブカテゴリの情報を取得する*/
			SubCategoryDao subCategoryDao = factory.getSubCategoryDao();
			allSubCategoryList = subCategoryDao.getSubCategories();
			
			Iterator subCategoryIterator = allSubCategoryList.iterator();
			while(subCategoryIterator.hasNext()){
				/*サブカテゴリのBeanを取得*/
				SubCategoryBean subCategory
				= (SubCategoryBean)subCategoryIterator.next();
				/*Beanの中のサブカテゴリの名前と、クライアントが選択した
				  サブカテゴリの名前が同じなら、そのサブカテゴリのIDを
				  変数に格納する*/
				if(subCategory.getSubCategoryName()
					.equals(selectedSubCategory)){
						selectedSubCategoryId
						= subCategory.getSubCategoryId();
					break;
				}
			}
			
			/*Daoから商品の情報を取得する*/
			ProductCatalogDao pcd = factory.getProductCatalogDao();
			
			/*全商品の情報*/
			/*sortArrayによって、どのようにソートさせるかを決定させている*/
			allCatalogList = pcd.getProductCatalogs(sortArray);
			
			/*Daoから商品の情報を取得する*/
			TagDao tagDao = factory.getTagDao();
			
			/*Tagから商品の情報を取得する*/
			allTagList = tagDao.getTags();
			
			/*選択されたサブカテゴリの商品が見つかった個数の変数を宣言*/
			int foundProductCount = 0;
			
			Iterator catalogIterator = allCatalogList.iterator();
			while(catalogIterator.hasNext()){
				/*Iteratorを使ってList内の商品情報のBeanを取り出す*/
				ProductCatalogBean product
					= (ProductCatalogBean)catalogIterator.next();
				
				/*選択されたサブカテゴリの商品なら、発見商品数を１増やす*/
				if(product.getSubCategoryId() == selectedSubCategoryId){
					foundProductCount++;
				}
				
				/*現在のページ番号で表示すべき商品なら、
				  その情報とそれに付加されているタグの名前をListに格納する*/
				if((pageNum - 1) * 15 < foundProductCount 
				&& foundProductCount <= pageNum * 15){
					
					/*現在の商品情報が持つタグの名前を格納するListの宣言*/
					List tagNames = new ArrayList();
					
					Iterator tagIterator = allTagList.iterator();
					while(tagIterator.hasNext()){
						TagBean tagRelation = (TagBean)tagIterator.next();
						/*現在の商品情報のIDがタグ表の行の商品IDと一致するなら、*/
						if(product.getExampleProductId()
						== tagRelation.getProductId()){
							/*そのタグの名前をArrayListに加える*/
							tagNames.add(tagRelation.getTagName());
						}
					}
					
					/*商品の情報と、付加されているタグの名前をMapに格納する*/
					Map productData = new HashMap();
					productData.put("productCatalog",product);
					productData.put("tagNames",tagNames);
					/*格納したMapをListに追加する*/
					returnProductsDataList.add(productData);
				}
			}
			
			
		}catch(IntegrationException e){
			throw new LogicException(e.getMessage(), e);
		}
		
		/*セッションスコープにインスタンスを保存*/
		requestContext.setSessionAttribute("pageNumber",pageNum);
		
		/*responseで送る値をセット*/
		responseContext.setResult(returnProductsDataList);
		
		/*転送先のビューを指定*/
		responseContext.setTarget("productlist");
		
		/*returnで結果を返す*/
		return responseContext;
	}
}