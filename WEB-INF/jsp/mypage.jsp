<%@page pageEncoding="UTF-8"
   contentType="text/html;charset=UTF-8"
   import="java.util.List"
   import="java.util.Iterator"
   import="java.util.ArrayList"
   import="bean.ProductImageBean"
   %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="ja">
	<head>
	<meta charset="utf-8">
	<title>MyPage</title>
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
	<script type="text/javascript" src="${pageContext.request.contextPath}/ddmenu_min.js"></script>
	</head>

	<body>

	<div id="container">

	<header>
	<h1 id="logo"><a href="${pageContext.request.contextPath}/front/top"><img src="${pageContext.request.contextPath}/images/logo.png" width="370" height="60" alt="Sample Online Shop"></a></h1>
	<div class="headermenu">
	<ul>
	<a href="${pageContext.request.contextPath}/front/userentry">会員登録</a>
	<a href="${pageContext.request.contextPath}/front/login">ログイン</a>
	</ul>
	<div id="cart"><a href="#">CART</a></div>
	</header>

	<nav id="menubar">
	<ul>
	<li class="arrow"><a>CATEGORY</a>
	    <ul class="ddmenu">
	    <li><a href="${pageContext.request.contextPath}/front/list">TOPS</a></li>
	    <li><a href="${pageContext.request.contextPath}/front/list">BOTTOMS</a></li>
	    <li><a href="${pageContext.request.contextPath}/front/list">UNDER</a></li>
	    <li><a href="${pageContext.request.contextPath}/front/list">SHOES</a></li>
	    <li><a href="${pageContext.request.contextPath}/front/list">ACCESSORIES</a></li>
		</ul>
	</li>
	<li class="arrow"><a href="${pageContext.request.contextPath}/front/menu_goods">SALE</a>
	</li>
	<li class="arrow"><a href="${pageContext.request.contextPath}/front/menu_others">RANKING</a>
	</li>
	<li class="arrow"><a href="${pageContext.request.contextPath}/front/menu_others">ALLITEM</a>
	</li>
	<li class="arrow"><a>HELP</a>
	    <ul class="ddmenu">
	    <li><a href="${pageContext.request.contextPath}/front/contact">お問い合わせ</a></li>
	    <li><a href="${pageContext.request.contextPath}/front/question">Q&A</a></li>
	</li>
	</ul>
	</nav>

	<div id="contents">


	<div id="main">

	<section>

	<h1><img src="${pageContext.request.contextPath}/images/icon.jpg" align="middle" width="50" height="50">
	プロフィール</h1>

	<section>
		<table class="ta1">
			<tr>
				<th>お名前</th>
				<td>${requestScope.member.memberName}</td>
			</tr>
			<tr>
				<th>メールアドレス</th>
				<td>${requestScope.member.memberEmail}</td>
			</tr>
			<tr>
				<th>生年月日</th>
				<td>${requestScope.member.memberBirthday}</td>
			</tr>
			<tr>
				<th>住所</th>
				<td>${requestScope.member.memberAddress}</td>
			</tr>
			<tr>
				<th>電話番号</th>
				<td>${requestScope.member.memberPhoneNumber}</td>
			</tr>
		</table>
	</section>

	<hr>
						<!--お気に入り一覧-->
						
						
	<h1>
		<img src="${pageContext.request.contextPath}/images/icon.jpg" align="middle" width="50" height="50">
		お気に入り一覧
	</h1>
	<br>
	<div style="width:740px; height:350px; overflow-x:auto;">
		<c:forEach var="favoriteMap" items="${favoriteInfoList}">
			<section class="list">
				<a href="${pageContext.request.contextPath}/front/item">
		    	<figure><img src="${pageContext.request.contextPath}/images/${favoriteMap.productsImageBean.productImagePath}"></figure>
				<h4>${favoriteMap.productBean.productName}<br>￥ ${favoriteMap.productBean.productPrice}</h4>
				</a>
			</section>
		</c:forEach>
	</div>
	<br>
	<hr>
					<!-- 会員限定セール -->
					
	<h1><img src="${pageContext.request.contextPath}/images/icon.jpg" align="middle" width="50" height="50">
	会員限定セール</h1>
	<br>
	<div style="width:740px; height:350px; overflow-x:auto;">
		<c:forEach var="adviceMap" items="${adviceInfoList}">
			<section class="list">
			    <a href="${pageContext.request.contextPath}/front/item">
			    <figure><img src="${pageContext.request.contextPath}/images/${adviceMap.productsImageBean.productImagePath}"></figure>
			    <h4>${adviceMap.productBean.productName}<br>￥ ${adviceMap.productBean.productPrice}</h4>
			    <span class="osusume">SALE</span>
			    </a>
			</section>
		</c:forEach>
	</div>
	</div>
	</div>
	<!--/main-->

	<div id="sub">

	<nav class="box1">
	<h2>MENU</h2>
	<ul class="submenu mb10">
	<li><a href="${pageContext.request.contextPath}/front/top">TOP</a></li>
	<li><a href="${pageContext.request.contextPath}/front/userconfig">アカウント設定</a></li>
	<li><a href="${pageContext.request.contextPath}/front/orderhistory?pageNum=1">購入履歴</a></li>
	<li><a href="${pageContext.request.contextPath}/front/leavecomp">退会手続き</a></li>
	</ul>
	</nav>


	</div>
	<!--/contents-->

	</div>
	<!--/container-->

	<footer>

	<div class="footermenu">
	<ul>
	<a href="${pageContext.request.contextPath}/front/companyinfo">会社概要　　　</a>
	<a href="${pageContext.request.contextPath}/front/tos">　　　利用規約</a>
	<a href="${pageContext.request.contextPath}/front/sitemap">　　　サイトマップ</a>
	<a href="${pageContext.request.contextPath}/front/privacypolicy">　　　個人情報保護方針</a>
	<a href="${pageContext.request.contextPath}/front/deal">　　　特定商取引法</a>
	<a href="${pageContext.request.contextPath}/front/contact">　　　お問い合わせ</a>
	<a href="${pageContext.request.contextPath}/front/question">　　　Q&A</a>
	<br>
	<br>
	</ul>

	<center><small>Copyright&copy; <a href="${pageContext.request.contextPath}/front/index">Sample Online Shop</a>　All Rights Reserved.</small>
	<span class="pr"><a href="http://template-party.com/" target="_blank">Web Design:Template-Party</a></span>

	</footer>
	<!--スマホ用メニューバー-->
	<img src="${pageContext.request.contextPath}/images/icon_bar.png" width="20" height="23" alt="" id="menubar_hdr" class="close">
	<script type="text/javascript">
	if (OCwindowWidth() < 480) {
		open_close("menubar_hdr", "menubar");
	}
	</script>

	</body>
</html>
