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
<meta name="description" content="�����ɃT�C�g���������܂�">
<meta name="keywords" content="�L�[���[�h�P,�L�[���[�h�Q,�L�[���[�h�R,�L�[���[�h�S,�L�[���[�h�T">
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
<a href="userentry.html">����o�^</a>
<a href="login.html">���O�C��</a>
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
    <li><a href="contact.html">���₢���킹</a></li>
    <li><a href="question.html">Q&A</a></li>
</li>
</ul>
</nav>

<div id="contents">


<div id="main">

<section>

<h1><img src="images/icon.jpg" align="middle" width="50" height="50">
�v���t�B�[��</h1>

<section>
<table class="ta1">
<tr>
<th>�����O</th>
<td>${data.memberName}</td>
</tr>
<tr>
<th>���[���A�h���X</th>
<td>${data.memberEmail}</td>
</tr>
<tr>
<th>���N����</th>
<td>${data.memberBirthday}</td>
</tr>
<tr>
<th>�Z��</th>
<td>${data.memberAddress}</td>
</tr>
<tr>
<th>�d�b�ԍ�</th>
<td>${data.memberPhoneNumber}</td>
</tr>
</table>

</section>

<hr>

<h1><img src="images/icon.jpg" align="middle" width="50" height="50">
���C�ɓ���ꗗ</h1>
<br>
 <section class="list">
    <a href="item.html">
    <figure><img src="images/outer8.png" alt="���i��"></figure>
    <h4>��W���P�b�g�@�@�@ \6,500</h4>
    </a>
 </section>
 <br><br>
<br><br><br><br><br><br><br>
<hr>

<h1><img src="images/icon.jpg" align="middle" width="50" height="50">
�������Z�[��</h1>
<br>

<section class="list">
    <a href="item.html">
    <figure><img src="images/boxer2.jpg" alt="���i��"></figure>
    <h4>�{�N�T�[�p���c<br> \1,000</h4>
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
<li><a href="tos.html">�A�J�E���g�ݒ�</a></li>
<li><a href="sitemap.html">�w������</a></li>
<li><a href="privacypolicy.html">�މ�葱��</a></li>
</ul>
</nav>


</div>
<!--/contents-->

</div>
<!--/container-->

<footer>

<div class="footermenu">
<ul>
<a href="companyinfo.html">��ЊT�v�@�@�@</a>
<a href="tos.html">�@�@�@���p�K��</a>
<a href="sitemap.html">�@�@�@�T�C�g�}�b�v</a>
<a href="privacypolicy.html">�@�@�@�l���ی���j</a>
<a href="deal.html">�@�@�@���菤����@</a>
<a href="contact.html">�@�@�@���₢���킹</a>
<a href="question.html">�@�@�@Q&A</a>
<br>
<br>
</ul>

<center><small>Copyright&copy; <a href="index.html">Sample Online Shop</a>�@All Rights Reserved.</small>
<span class="pr"><a href="http://template-party.com/" target="_blank">Web Design:Template-Party</a></span>

</footer>
<!--�X�}�z�p���j���[�o�[-->
<img src="images/icon_bar.png" width="20" height="23" alt="" id="menubar_hdr" class="close">
<script type="text/javascript">
if (OCwindowWidth() < 480) {
	open_close("menubar_hdr", "menubar");
}
</script>

</body>
</html>
