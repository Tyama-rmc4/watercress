<%@page pageEncoding="Windows-31J"
   contentType="text/html;charset=Windows-31J"
   import="java.util.List"
   import="java.util.Iterator"
   import="java.util.ArrayList"
   %>
<!doctype html>
<html lang="ja">
<head>
<meta charset="utf-8">
<title>MyPage</title>
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

<body>

<div id="container">
<%
	List data = request.getAttribute("data");
	
%>
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
    <li><a href="list.html">BOTTOMS</a></li>
    <li><a href="list.html">UNDER</a></li>
    <li><a href="list.html">SHOES</a></li>
    <li><a href="list.html">ACCESSORIES</a></li>
	</ul>
</li>
<li class="arrow"><a href="menu_goods.html">SALE</a>
</li>
<li class="arrow"><a href="menu_others.html">RANKING</a>
</li>
<li class="arrow"><a href="menu_others.html">ALLITEM</a>
</li>
<li class="arrow"><a>HELP</a>
    <ul class="ddmenu">
    <li><a href="contact.html">お問い合わせ</a></li>
    <li><a href="question.html">Q&A</a></li>
</li>
</ul>
</nav>

<div id="contents">


<div id="main">

<section>

<h1><img src="images/icon.jpg" align="middle" width="50" height="50">
プロフィール</h1>

<section>
<table class="ta1">
<tr>
<th>お名前</th>
<td>${data.memberName}</td>
</tr>
<tr>
<th>メールアドレス</th>
<td>${data.memberEmail}</td>
</tr>
<tr>
<th>生年月日</th>
<td>${data.memberBirthday}</td>
</tr>
<tr>
<th>住所</th>
<td>${data.memberAddress}</td>
</tr>
<tr>
<th>電話番号</th>
<td>${data.memberPhoneNumber}</td>
</tr>
</table>

</section>

<hr>

<h1><img src="images/icon.jpg" align="middle" width="50" height="50">
お気に入り一覧</h1>
<br>
 <section class="list">
    <a href="item.html">
    <figure><img src="images/outer8.png" alt="商品名"></figure>
    <h4>皮ジャケット　　　 \6,500</h4>
    </a>
 </section>
 <br><br>
<br><br><br><br><br><br><br>
<hr>

<h1><img src="images/icon.jpg" align="middle" width="50" height="50">
会員限定セール</h1>
<br>

<section class="list">
    <a href="item.html">
    <figure><img src="images/boxer2.jpg" alt="商品名"></figure>
    <h4>ボクサーパンツ<br> \1,000</h4>
    <span class="osusume">SALE</span>
    </a>
    </section>

</div>

</section>


</div>
<!--/main-->

<div id="sub">

<nav class="box1">
<h2>MENU</h2>
<ul class="submenu mb10">
<li><a href="companyinfo.html">TOP</a></li>
<li><a href="tos.html">アカウント設定</a></li>
<li><a href="sitemap.html">購入履歴</a></li>
<li><a href="privacypolicy.html">退会手続き</a></li>
</ul>
</nav>


</div>
<!--/contents-->

</div>
<!--/container-->

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

<center><small>Copyright&copy; <a href="index.html">Sample Online Shop</a>　All Rights Reserved.</small>
<span class="pr"><a href="http://template-party.com/" target="_blank">Web Design:Template-Party</a></span>

</footer>
<!--スマホ用メニューバー-->
<img src="images/icon_bar.png" width="20" height="23" alt="" id="menubar_hdr" class="close">
<script type="text/javascript">
if (OCwindowWidth() < 480) {
	open_close("menubar_hdr", "menubar");
}
</script>

</body>
</html>
