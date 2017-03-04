<!--
式言語で取得できるデータ
 resultData : Map<String, Object> 【式言語で${data}で取り出される部分】
 ┃
 ┗"productCount",Integer 検索条件に該当する商品の数
 ┃
 ┗"pageNumber",Integer 表示するページ番号
 ┃
 ┗"productData" : List<Map>
   ┗productData : Map<String, Object> 商品一件ずつのデータ
     ┃
     ┗"catalog",ProductCatalogBean このBeanの内容通りの、名前などのデータ
     ┗"tagNames",List<String> その商品に付加されているタグの名前のList
     ┗"colors",List<String> その商品の色の画像パスのList
     ┗"isFavorite",Boolean その商品はログイン中の会員のお気に入りであるか
     ┗"categoryName",String その商品のカテゴリ名
-->


<%@page pageEncoding="UTF-8"
   contentType="text/html;charset=UTF-8"
   %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!doctype html>
<html lang="ja">
<head>
<meta charset="utf-8">
<title>Watercress_TOP</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="copyright" content="Template Party">
<meta name="description" content="ここにサイト説明を入れます">
<meta name="keywords" content="キーワード１,キーワード２,キーワード３,キーワード４,キーワード５">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<!--[if lt IE 9]>
<script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
<style>.ddmenu {display: none;}</style>
<![endif]-->
<script type="text/javascript" src="${pageContext.request.contextPath}/js/openclose.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ddmenu_min.js"></script>
</head>

<body id="top" class="c1">

<div id="container">

<header>
<h1 id="logo"><a href="top.html"><img src="images/logo.png" width="370" height="60" alt="Sample Online Shop"></a></h1>
<div class="headermenu">
<ul>
<a href="userentry.html">会員登録</a>
<a href="login.html">ログイン</a>
</ul>
<div id="cart"><a href="#">CART</a></div>
</header>

<nav id="menubar">
<ul>
<li class="arrow"><a>CATEGORY</a>
    <ul class="ddmenu">
    <li><a href="productlist?category=tops">TOPS</a></li>
    <li><a href="productlist?category=bottoms">BOTTOMS</a></li>
    <li><a href="productlist?category=under">UNDER</a></li>
    <li><a href="productlist?category=shoes">SHOES</a></li>
    <li><a href="productlist?category=accessories">ACCESSORIES</a></li>
    <!--<li><a href="productlist?subCategory=outer">サブカテゴリのリンクテスト：outer</a></li>-->
    </ul>
</li>
<li class="arrow"><a href="productlist?searchTag=セール">SALE</a>
</li>
<li class="arrow"><a href="productlist?sort=purchaseDesc">RANKING</a>
</li>
<li class="arrow"><a href="productlist">ALLITEM</a>
</li>
<li class="arrow"><a>HELP</a>
    <ul class="ddmenu">
    <li><a href="contact.html">お問い合わせ</a></li>
    <li><a href="question.html">Q&A</a></li>
</li>
</ul>
</nav>
<br/>
<br/>
<br/>
<div>
	<form action="productlist" method="get">
		<input type="hidden" name="category" value="${param.category}">
		<input type="hidden" name="subCategory" value="${param.subCategory}">
		<input type="hidden" name="pageNumber" value="1">
		<h3>検索</h3>
		名前
		<input type="text" name="searchText" size="40">
		タグ
		<select name="searchTag">
			<option value=""></option>
			<option value="冬季限定">冬季限定</option>
			<option value="セール">セール</option>
		</select>
		<br/>
		並び替え
		<br/>
		名前順
		<select name="sort">
			<option value=""></option>
			<option value="nameAsc">五十音順</option>
			<option value="nameDesc">五十音の逆順</option>
		</select>
		値段順
		<select name="sort">
			<option value=""></option>
			<option value="priceAsc">値段の安い順</option>
			<option value="priceDesc">値段の高い順</option>
		</select>
		購入数順
		<select name="sort">
			<option value=""></option>
			<option value="purchaseDesc">購入数が多い順</option>
			<option value="purchaseAsc">購入数が少ない順</option>
		</select>
		<input type="submit" value="検索">
	</form>
</div>

<h1>商品一覧</h1>

<!-- テスト用パラメータ取得
選択カテゴリ：${param.category}
選択サブカテゴリ：${param.subCategory}
並べ替え：${paramValues.sort[0]} ${paramValues.sort[1]} ${paramValues.sort[2]}
-->
<%
	int productCount = 0;
%>
<table style="table-layout: fixed;">
<tr>
<c:forEach var="product" items="${data.productsData}">
	<%
		productCount += 1;
		pageContext.setAttribute("productCount",productCount);
	%>
	
	<td width="194" height="320">
		<section class="list" style="position:relative; height:100%;">
			<figure>
				<a href ="productdetail?productName=${product.catalog.productName}">
					<img src="${pageContext.request.contextPath}/images/${product.categoryName}/
					${product.catalog.productImagePath}" alt="商品の画像">
					
					
					<!-- 各タグの表示 -->
					<c:forEach var="tagName" items="${product.tagNames}">
						<c:if test="${tagName == '冬季限定'}">
							<!--
							<img class="冬季限定タグ画像のクラス" 
							src="${pageContext.request.contextPath}/images/tag.hoge" alt="冬季限定">
							-->
						</c:if>
						<c:if test="${tagName == 'セール'}">
							<!--
							<img class="セールタグ画像のクラス" 
							src="${pageContext.request.contextPath}/images/tag.hoge" alt="セール">
							-->
						</c:if>
					</c:forEach>
					
					<!-- 売り切れの表示 -->
					<c:if test="${product.catalog.productStockCount == 0}">
						<span class="sumi">SOLD OUT</span>
					</c:if>
					
					</figure>
					<h4 style="position: absolute">${product.catalog.productName}
					<br/> ￥${product.catalog.productPrice}<br/></h4>
				</a>
		</section>
	</td>
	<c:if test="${pageScope.productCount % 5 == 0}" >
	</tr>
	<tr height="80">
	</tr>
	<tr>
	</c:if>
</c:forEach>
</tr>
</table>

<!-- 現在のページが1ページ目でなければ、前のページへ移動するリンクを表示 -->
<c:if test="${data.pageNumber > 1}" >
	<a href ="productlist?category=${param.category}
	&subCategory=${param.subCategory}&pageNumber=${data.pageNumber-1}
	&sort=${paramValues.sort[0]}&sort=${paramValues.sort[1]}&sort=${paramValues.sort[2]}">
	前のページへ</a>
</c:if>

<!-- 商品数１５毎に１個、ページ移動ボタンを増やす -->
<%
	int pageCount = 0;
%>
<c:forEach items="${data.productCount}" step="15">
		<%
			pageCount += 1;
			pageContext.setAttribute("pageCount",pageCount);
		%>
	<a href ="productlist?category=${param.category}
	&subCategory=${param.subCategory}&pageNumber=${pageScope.pageCount}
	&sort=${paramValues.sort[0]}&sort=${paramValues.sort[1]}&sort=${paramValues.sort[2]}">
	${pageScope.pageCount}</a>
</c:forEach>

<!-- 現在のページが最後のページでなければ、次のページへ移動するリンクを表示 -->
<c:if test="${data.pageNumber < pageScope.pageCount}" >
	<a href ="productlist?category=${param.category}
	&subCategory=${param.subCategory}&pageNumber=${data.pageNumber+1}
	&sort=${paramValues.sort[0]}&sort=${paramValues.sort[1]}&sort=${paramValues.sort[2]}">
	次のページへ</a>
</c:if>

<footer>
<div class="footermenu">
<ul>
<a href="companyinfo.html">会社概要　　　</a>
<a href="tos.html">　　　利用規約</a>
<a href="sitemap.html">　　　サイトマップ</a>
<a href="privacypolicy.html">　　　個人情報保護方針</a>
<a href="deal.html">　　　特定商取引法</a>
<a href="contact.html">　　　お問い合わせ</a>
<a href="question.html">　　　Q&A</a>
<br>
<br>
</ul>

<center><small>Copyright&copy; <a href="top.html">Sample Online Shop</a>　All Rights Reserved.</small>
<span class="pr"><a href="http://template-party.com/" target="_blank">Web Design:Template-Party</a></span>

</footer>
</body>
</html>
