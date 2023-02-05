<?php

function generateLink($url, $label, $class) {
    $link = '<a href="' . $url . '" class="' . $class . '">';
    $link .= $label;
    $link .= '</a>';
    return $link;
}

function outputPostRow($post) {
    $postLink = 'post.php?id=' . $post["postId"];
    $image = '<img src="images/' . $post["thumb"] . '" alt="' . $post["title"] . '" class="img-responsive"/>';

    $user = utf8_encode($post["username"]);
    $userLink = generateLink("user.php?id=" . $post["userId"], $user, null);

    echo '<div class="row">';
    echo generateLink($postLink, $image, null);
    echo '</div>';
    echo '<div class="col-md-8"> ';
            echo '<h2>' . $post["title"] . '</h2>';
            echo '<div class="details">';
                echo 'Posted by ' . $userLink;
                echo '<span class="pull-right">' . $post["date"] . '</span>';
                echo '<p class="ratings">';
    
                echo constructRating($post["reviewsRating"]);
                echo ' ' . $post["reviewsNum"] .' Reviews';    
                echo '</p>';
            echo '</div>';
            echo '<p class="excerpt">';
            echo $post["excerpt"];
            echo '</p>';
            echo '<p>' . generateLink($postLink, 'Read more', 'btn btn-primary btn-sm') . '</p>';
        echo '</div>';
    echo '</div>';
    echo '<hr/>';
}

function constructRating($rating) {
    $imgTags = "";
    
    // first output the gold stars
    for ($i=0; $i < $rating; $i++) {
        $imgTags .= '<img src="images/star-gold.svg" width="16" />';
    }
    
    // then fill remainder with white stars
    for ($i=$rating; $i < 5; $i++) {
        $imgTags .= '<img src="images/star-white.svg" width="16" />';
    }    
    
    return $imgTags;    
}

?>