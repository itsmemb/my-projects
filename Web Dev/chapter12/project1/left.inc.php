<aside class="col-md-2">
    <div class="panel panel-info">
        <div class="panel-heading">Continents</div>
        <ul class="list-group">
            <?php 
                foreach($continents as $c) {
                    echo "<li class='list-group-item'><a href='#'>$c</a></li>";
                }
            ?>
        </ul>
    </div>
    <!-- end continents panel -->

    <div class="panel panel-info">
        <div class="panel-heading">Popular</div>
        <ul class="list-group">
            <?php
                foreach($countries as $key => $value) {
                    echo "<li class='list-group-item'>" . generateLink("photos.php?iso=$key",$value,null) . "</li>";
                }
            ?>
        </ul>
    </div>
    <!-- end continents panel -->
</aside>