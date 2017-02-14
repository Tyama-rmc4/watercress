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
<link rel="stylesheet" href="css/style.css">
<!--[if lt IE 9]>
<script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
<style>.ddmenu {display: none;}</style>
<![endif]-->
<script type="text/javascript" src="js/openclose.js"></script>
<script type="text/javascript" src="ddmenu_min.js"></script>
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
    <li><a href="list.html">TOPS</a></li>
    <li><a href="productlist?subCategory=outer">サブカテゴリのリンクテスト：outer</a></li>
    <li><a href="list2.html">BOTTOMS</a></li>
    <li><a href="list3.html">UNDER</a></li>
    <li><a href="list4.html">SHOES</a></li>
    <li><a href="list5.html">ACCESSORIES</a></li>
    </ul>
</li>
<li class="arrow"><a href="productlist.html">SALE</a>
</li>
<li class="arrow"><a href="productlist.html">RANKING</a>
</li>
<li class="arrow"><a href="productlist.html">ALLITEM</a>
</li>
<li class="arrow"><a>HELP</a>
    <ul class="ddmenu">
    <li><a href="contact.html">お問い合わせ</a></li>
    <li><a href="question.html">Q&A</a></li>
</li>
</ul>
</nav>

<div>
	検索
	<form action="productlist" method="get">
		<input type="hidden" name="subCategory" value="${param.subCategory}">
		<input type="hidden" name="pageNumber" value="1">
		<input type="text" name="searchText" size="40">
		<input type="submit" value="検索">
	</form>
</div>

<h1>商品一覧</h1>
選択サブカテゴリ：${param.subCategory}
<c:forEach var="product" items="${data.productsData}">
	
	<section class="list">
		<a href="item.html">
			<figure>
				<a href ="productdetail?productName=${product.catalog.productName}">
					<img src="${pageContext.request.contextPath}/images/${product.categoryName}/${product.catalog.productImagePath}" alt="商品の画像">
				</a>
				
				<!-- 各タグの表示 -->
				<c:forEach var="tagName" items="${product.tagNames}">
					<c:if test="${tagName == 'タグ名'}">
						<img class="タグ画像のクラス" src="${pageContext.request.contextPath}/images/tag.hoge" alt="商品名">
					</c:if>
				</c:forEach>
				
				<!-- 売り切れの表示 -->
				<c:if test="${product.catalog.productStockCount == 0}">
					<img class="売り切れ画像のクラス" src="${pageContext.request.contextPath}/images/soldout.hoge" alt="売り切れ">
				</c:if>
				
			</figure>
			<h4>${product.catalog.productName} ￥${product.catalog.productPrice} </h4>
		</a>
	</section>
</c:forEach>

<!-- 現在のページが1ページ目でなければ、前のページへ移動するリンクを表示 -->
<c:if test="${data.pageNumber > 1}" >
	<a href ="productlist?subCategory=${param.subCategory}&pageNumber=${data.pageNumber-1}">前のページへ</a>
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
	<a href ="productlist?subCategory=${param.subCategory}&pageNumber=${pageScope.pageCount}">${pageScope.pageCount}</a>
</c:forEach>

<!-- 現在のページが最後のページでなければ、次のページへ移動するリンクを表示 -->
<c:if test="${data.pageNumber < pageScope.pageCount}" >
	<a href ="productlist?subCategory=${param.subCategory}&pageNumber=${data.pageNumber+1}">次のページへ</a>
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
