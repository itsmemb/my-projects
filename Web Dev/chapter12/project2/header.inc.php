<header class="header">  
  <div class="header__wrapper">
    <h1 class="header__heading">Art Store</h1>
    <nav class="navigation">
      <ul class="navigation__list">
        <?php
            foreach($links as $a) {
              echo "<li class='navigation__list-item'><a href='" . $a['url'] . "'>" . $a['label'] . "</a></li>";
            }
        ?>
      </ul>
    </nav>  
  </div>
</header> 