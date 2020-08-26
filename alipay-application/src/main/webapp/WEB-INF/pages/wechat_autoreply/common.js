<script type="text/html" id="item-select-result-template">
     <form id="news-form" action="${base}/#CONTROLLER_KEY#/saveNews/#ID#" method="post">
         <input type="hidden" id="news-title" name="wechatAutoreply.title" value="#TITLE#" />
         <input type="hidden" id="news-digest" name="wechatAutoreply.digest" value="#DIGEST#"/>
         <input type="hidden" id="news-create_time" name="createTime" value="#CREATE_TIME#"/>
         <input type="hidden" id="news-content" name="wechatAutoreply.content" value="#CONTENT#"/>
         <input type="hidden" id="news-url" name="wechatAutoreply.url"  value="#URL#" />
         <input type="hidden" id="news-thumb_url" name="wechatAutoreply.thumb_url" value="#THUMB_URL#" />
     </form>
</script>