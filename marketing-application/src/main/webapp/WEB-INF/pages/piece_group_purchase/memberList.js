<script>
    var timer;
    var timeOver;
    var timeDiv;
    $(document).ready(function() {
        timeOver = new Date().setTime(${pieceGroupPurchaseMaster.end_time.getTime()});
        timeDiv = $('#time');
        timer = setInterval("times()",1000);
    });

    function times() {
        var timeOut = new Date();
        var s = Math.round((timeOver - timeOut)/1000);
        if(s>0) {
            var d = Math.floor(s/(3600*24));
            d<10 && (d='0' + d);
            //计算剩余小时h
            var h = Math.floor(s/3600%24);
            h<10 && (h='0' + h);
            //计算剩余时间m
            var m = Math.floor(s/60%60);
            m<10 && (m='0' + m);
            //计算剩余s
            s%=60;
            s<10 && (s='0' + s);
            timeDiv.html(d + '天' + h + '时' + m + '分' +s + '秒');
        } else {
            clearInterval(timer);
            timer=null;
            timeDiv.html('');
        }
    }
</script>