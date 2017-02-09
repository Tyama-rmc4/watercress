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

/**
 *@className ShowProductsListCommand
 *@author 河野
 *@date 2017/01/31 ページ番号をセッションで管理する修正をしました。
 *@author 池田
 *@date 2017/02/07 格納する結果の仕様を、タグの名前を含むものに変更しました。
 *@date 2017/02/08 商品名、タグ名での検索を行うことができるようにしました。
 *@date 2017/02/09 結果のproductDataに、商品の色を表す画像のパスのListである
                   "productColors"を追加しました。
 *@description 
 data : List<Map> 【jspで${data}で取り出される部分】
 ┃
 ┗productData : Map<String, Object>
   ┃
   ┗"productCatalog",ProductCatalogBean
   ┗"productTagNames",List<String>
   ┗"productColors",List<String>
 */

public class ShowProductsListCommand extends AbstractCommand{
	
	public ResponseContext execute(ResponseContext responseContext)
	throws LogicException{
		/*RequestContextのインスタンスを取得*/
		RequestContext requestContext = getRequestContext();
		
		/*送信されてきた各パラメータの取得*/
		/*ページの番号*/
		int pageNumber
		= Integer.parseInt(requestContext.getParameter("pageNumber")[0]);
		/*選択されたサブカテゴリの名前*/
		String selectedSubCategory
		= requestContext.getParameter("subCategory")[0];
		/*検索する名前  全角スペースを半角スペースに変換した後、
		  半角スペースで分割して配列にする*/
		String searchTextParam
		= requestContext.getParameter("searchText")[0];
		String[] searchTexts = null;
		if(searchTextParam != null){
			searchTextParam = searchTextParam.replaceAll("　"," ");
			searchTexts = searchTextParam.split(" ");
		}
		/*検索するタグ  全角スペースを半角スペースに変換した後、
		  半角スペースで分割して配列にし、Listにする*/
		String searchTagParam = requestContext.getParameter("searchTag")[0];
		String[] searchTagArray = null;
		List searchTags = null;
		if(searchTagParam != null){
			searchTagParam = searchTagParam.replaceAll("　"," ");
			searchTagArray = searchTagParam.split(" ");
			searchTags = new ArrayList(Arrays.asList(searchTagArray));
		}
		/*ソート条件*/
		String[] sortParams = requestContext.getParameter("sort");
		
		/*getProductCatalogsに渡す、ソート条件の引数を作成する*/
		/*sortParamsの文字列を、createSortArrayメソッドのルールに
		  従ってint配列に変換する。これを商品検索の際に引数にする*/
		int[] sortArray = createSortArray(sortParams);
		
		/*全サブカテゴリの情報のリスト*/
		List allSubCategoryList = null; 
		/*全商品の情報のリスト*/
		List allCatalogList = null; 
		/*全タグの情報のリスト*/
		List allTagList = null;
		
		/*このコマンドのResultとなる、商品情報とそのタグ名のリスト*/
		List<Map> returnProductsDataList = new ArrayList<Map>();
		
		/*選択されたサブカテゴリのIDを格納する変数を宣言*/
		int selectedSubCategoryId = 0;
		try{
			/*Daoを取得するためのDaoFactoryを取得*/
			AbstractDaoFactory factory = AbstractDaoFactory.getFactory();
			
			/*Daoからサブカテゴリの情報を取得する*/
			SubCategoryDao subCategoryDao = factory.getSubCategoryDao();
			allSubCategoryList = subCategoryDao.getSubCategories();
			
			/*Iteratorを使い、各サブカテゴリの情報を確認*/
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
			
			/*商品の情報を取得するためのDaoを取得する*/
			ProductCatalogDao pcd = factory.getProductCatalogDao();
			/*Daoから商品の情報を取得する。sortArrayによってソート条件を設定*/
			allCatalogList = pcd.getProductCatalogs(sortArray);
			
			/*タグの情報を取得するためのDaoを取得する*/
			TagDao tagDao = factory.getTagDao();
			/*Daoからタグの情報を取得する*/
			allTagList = tagDao.getTags();
			
			/*お気に入りの取得も必要だろうか？*/
			
			/*選択されたサブカテゴリの商品が見つかった個数の変数を宣言*/
			int foundProductCount = 0;
			
			Iterator catalogIterator = allCatalogList.iterator();
			while(catalogIterator.hasNext()){
				/*Iteratorを使ってList内の商品情報のBeanを取り出す*/
				ProductCatalogBean product
					= (ProductCatalogBean)catalogIterator.next();
				
				/*選択したサブカテゴリの商品なら、次の検索合致判定に進む*/
				if(product.getSubCategoryId() == selectedSubCategoryId){
					
					/*その商品に付加されているタグをこの時点で取得しておく*/
					/*現在の商品情報のタグの名前を格納するListの宣言*/
					List<String> productTagNames = new ArrayList<String>();
						
					Iterator tagIterator = allTagList.iterator();
					while(tagIterator.hasNext()){
						TagBean tagRelation = (TagBean)tagIterator.next();
						/*現在の商品情報のIDが
						  タグ表の行の商品IDと一致するなら、*/
						if(product.getExampleProductId()
						== tagRelation.getProductId()){
							/*そのタグの名前をArrayListに加える*/
							productTagNames.add(tagRelation.getTagName());
						}
					}
					
					/*検索条件の文字列に合致するかを持つ変数の宣言*/
					boolean isMatchText = false;
					/*検索条件のタグに合致するかを持つ変数の宣言*/
					boolean isMatchTag = false;
					
					/*検索条件の文字列が存在しないなら常に条件合致とする*/
					if(searchTexts == null){
						isMatchText = true;
					}else{
						/*検索条件の文字列が存在する場合、
						  検索文字列全てと合致するかを判定する*/
						/*「全ての検索文字列を持っている」を持つ変数を宣言*/
						boolean isAllTextExist = true;
						for(String searchText : searchTexts){
							/*商品名が検索文字列を含んでいないなら*/
							if(product.getProductName().indexOf(searchText)
								== -1){
								/*「全ての検索文字列を持っている」を偽にする*/
								isAllTextExist = false;
							}
						}
						/*「全ての検索文字列を持っている」が真なら*/
						if(isAllTextExist){
							/*「文字列の検索条件に合致する」ことを変数に格納*/
							isMatchText = true;
						}
					}
					/*検索条件のタグが存在しないなら常に条件合致とする*/
					if(searchTags == null){
						isMatchTag = true;
					}else{
						/*検索条件のタグが存在する場合、
						  検索タグ全てと合致するかを判定する*/
						/*「全ての検索タグを持っている」を持つ変数を宣言*/
						boolean isAllTagExist = true;
						Iterator searchTagIterator = searchTags.iterator();
						while(searchTagIterator.hasNext()){
							String searchTagName
							= (String)searchTagIterator.next();
							/*商品に付加されているタグの中に
							  検索条件のタグが無いなら*/
							if(!(productTagNames.contains(searchTagName)) ){
								/*「全ての検索タグを持っている」を偽にする*/
								isMatchText = false;
							}
						}
						/*「全ての検索タグを持っている」が真なら*/
						if(isAllTagExist){
							/*「文字列のタグ条件に合致する」ことを変数に格納*/
							isMatchTag = true;
						}
					}
					/*検索条件の文字列とタグに合致するなら、商品発見後処理へ*/
					if(isMatchText && isMatchTag){
						/*発見商品数を１増やす*/
						foundProductCount++;
						
						/*現在のページでその商品を表示すべきかを判定する*/
						/*現在のページ番号で表示すべき商品なら、その情報と
						  それに付加されているタグの名前と
						  その商品の色を表す画像のパスを
						  Mapに格納し、それをListに格納する*/
						if((pageNumber - 1) * 15 < foundProductCount 
						&& foundProductCount <= pageNumber * 15){
							
							/*商品の情報と、付加されているタグの名前と
							  商品の色を表す画像のパスを
							  Mapに格納する。*/
							/*商品の色を表す画像のパスは
							  分割したメソッドから取得する*/
							Map productData = new HashMap();
							productData.put("productCatalog",product);
							productData.put("productTagNames",productTagNames);
							List<String> productColors
							= getProductColors(product.getProductName());
							productData.put("productColors",productColors);
							/*格納したMapをListに追加する*/
							returnProductsDataList.add(productData);
						}/*if(現在のページで表示すべきか) の終端*/
					}/*if(検索文字列、検索タグに合致するか)*/
				}/*if(選択したサブカテゴリか) の終端*/
			}/*while(catalogIterator.hasNext()) の終端*/
			
		}catch (IntegrationException e){
			throw new LogicException(e.getMessage(), e);
		}
		
		/*セッションスコープに表示するページ番号を保存*/
		requestContext.setSessionAttribute("pageNumber",pageNumber);
		
		/*responseで送る値をセット*/
		responseContext.setResult(returnProductsDataList);
		
		/*転送先のビューを指定*/
		responseContext.setTarget("productlist");
		
		/*returnで結果を返す*/
		return responseContext;
	}
	
	private int[] createSortArray(String[] sortParams){
		/*返却するソート方法指定用配列を宣言*/
		int[] sortArray = new int[3];
		
		/*sortParamsの内容に従って、定数を配列に格納する*/
		for(int i = 0;i<=sortParams.length;i++){
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
		/*作成した配列を返す*/
		return sortArray;
	}
	
	/*引数の商品名と名前が一致する商品の、色の画像パスのリストを返すメソッド*/
	private List<String> getProductColors(String productName)
	throws LogicException{
		/*このメソッドが返す、色の画像パスのリストの変数を宣言*/
		List<String> productColors = new ArrayList<String>();
		
		/*プロパティファイルへのパス */
		String FILE_PATH = "c:/watercress/ProductColors.properties";
		
		/*色毎の画像パスを保存しているプロパティファイルを読み込む*/
		Properties properties = new Properties();
		
		try{
			properties.load(new FileInputStream(FILE_PATH));
			
			/*Daoを取得するためのDaoFactoryを取得*/
			AbstractDaoFactory factory = AbstractDaoFactory.getFactory();
			/*商品情報を取得するためのProductDaoを取得*/
			ProductDao productDao = factory.getProductDao();
			
			/*全商品の情報を取得*/
			List allProductList = productDao.getProducts();
			
			Iterator productIterator = allProductList.iterator();
			while(productIterator.hasNext()){
				
				/*商品の情報１件のBeanを取得*/
				ProductBean product = (ProductBean)productIterator.next();
				
				/*引数の商品名と現在の商品の名前と一致するなら*/
				if(productName.equals(product.getProductName())){
					/*現在の商品の色に対応する画像パスを取得する*/
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