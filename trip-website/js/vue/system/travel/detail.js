var user = getUserInfo();
//(扮酷小蚂)  ---><img....>
//将 (中文)   格式数据替换成标签图片:
function emoji(str) {
    //表情图片资源, 从马蜂窝扣出来的, 可以百度:
    //1:HttpURLConnection 使用  jdk自带的 代码中如何发起http请求
    //   RestTemplate  项目发短信用到
    //2:HttpClient  第三方http请求发送的框架
    //RestTemplate(client)
    //3:webmagic  专门用于爬虫框架

    //str=现金付款山东省看得(大笑小蜂)见风科技适(大笑小蜂)得府君书(得意小蜂)的开发决胜巅峰

    //匹配中文
    var reg = /\([\u4e00-\u9fa5A-Za-z]*\)/g;
    var matchArr = str.match(reg);  //[(大笑小蜂), 大笑小蜂),(得意小蜂)]
    if (!matchArr) {
        return str;
    }
    for (var i = 0; i < matchArr.length; i++) {
        str = str.replace(matchArr[i], '<img src="' + EMOJI_MAP[matchArr[i]] + '"style="width: width:28px;"/>')
    }
    return str;
}

var vue = new Vue({
    el: "#app",
    data: {
        param: {},
        page: {},
        content: {}, //内容
        detail: {
            coverUrl: "",
            author: {},
            dest: {}
        },
        dest: {},
        toasts: [],
        strategies: [],
        comments: [],
        travels: []
    },
    methods: {
        //表情处理-列表
        emotionClick: function (e) {
            e.stopPropagation();
            $('#_js_replyExpression').show();
        },
        //表情处理-单项
        emojiitemClick: function (e) {
            $('._j_replyarea').val($('._j_replyarea').val() + '(' + $(e.currentTarget).attr('title') + ')')
        },
        //表情页签
        faceTabClick: function (e) {
            e.stopPropagation();
            $('._j_face_tab').removeClass('cur');
            $(e.currentTarget).addClass('cur');
            $('.art_newface').addClass('hide');
            $('.art_newface').eq($(e.currentTarget).index()).removeClass('hide');
        },
        //表情页签-下一页
        newtabNext: function (e) {
            e.stopPropagation();
            if (!$(e.currentTarget).hasClass('disabled')) {
                $(e.currentTarget).addClass('disabled')
                $('.newtab-prev').removeClass('disabled')
                $('._j_switch_container').css('left', '-324px');
            }
        },
        //表情页签-上一页
        newtabPrev: function (e) {
            e.stopPropagation();
            if (!$(e.currentTarget).hasClass('disabled')) {
                $(e.currentTarget).addClass('disabled')
                $('.newtab-next').removeClass('disabled')
                $('._j_switch_container').css('left', '0');
            }
        },
        queryStatData: function () {
            ajaxGet("article", "/travels/stat/data", {id: this.strategy.id}, function (data) {
                let map = data.data;
                vue.detail.viewnum = map.viewnum;
                vue.detail.replynum = map.replynum;
                vue.detail.sharenum = map.sharenum;
                vue.detail.favornum = map.favornum;
                vue.detail.likesnum = map.likesnum;
            })
        },
        //回复
        toComment: function (nickname, refId) {
            $("#commentTpye").val(1);
            $("#refCommentId").val(refId);
            $("#commentContent").focus();
            $("#commentContent").attr("placeholder", "回复：" + nickname);

        },
        //评论添加
        commentAdd: function (e) {
            var content = $("#commentContent").val();
            if (!content) {
                popup("评论不能为空");
                return;
            }
            var param = {};
            param.travelId = vue.detail.id;
            param.travelTitle = vue.detail.title;
            param.content = emoji(content);

            param.type = $("#commentTpye").val();

            if(param.type == 1){
                param["refComment.id"] = $("#refCommentId").val();
            }else{
                param["refComment.id"] = "";
            }
            $("#commentTpye").val(0);
            $("#refCommentId").val("");

            ajaxPost("comment", "/travels/comments/save", param, function (data) {
                $("#commentContent").val("");
                $("#commentContent").attr("placeholder", "");
                vue.queryComments(param.travelId);
            })
        },
        //评论查询
        queryComments: function (travelId) {
            travelId = travelId || getParams().id;
            //游记评论不分页
            ajaxGet("comment", "/travels/comments/query", {travelId: travelId}, function (data) {
                vue.comments = data.data;
            })
        },

        //游记明细
        queryDetail: function () {
            //游记
            ajaxGet("article", "/travels/detail", {id: this.param.id}, function (data) {
                vue.detail = data.data;
                vue.content = data.data.content
                vue.param.destId = vue.detail.destId;
                if (user) {
                    //统计数据
                }
            })
        },
        //游记点赞
        travelLikes: function () {
            ajaxPost("u", "/users/likes", {articleId: this.param.id, type : -1}, function (data) {
                if (data.data) {
                    popup("顶成功啦");
                    // 刷新统计数据
                    vue.queryStatData();
                } else {
                    popup("今天你已经顶过了");
                }
            })
        },
        //游记收藏
        favor: function () {
            ajaxPost("u", "/users/favor", {articleId: this.param.id, type: -1}, function (data) {
                if (data.data) {
                    popup("收藏成功");
                    vue.detail.favorite = true;
                } else {
                    popup("已取消收藏");
                    vue.detail.favorite = false;
                }
                // 刷新统计数据
                vue.queryStatData();
            })
        },
        //吐司
        queryToasts: function () {
            ajaxGet("article", "/destinations/toasts", {destId: this.param.destId}, function (data) {
                vue.dest = data.data.pop();
                vue.toasts = data.data
            })
        },
        //攻略点击量前3
        queryStrViewnumTop3: function () {
            ajaxGet("article", "/strategies/viewnumTop3", {destId: this.param.destId}, function (data) {
                vue.strategies = data.data;
            });
        },
        //游记点击量前3
        queryTravelViewnumTop3: function () {
            ajaxGet("article", "/travels/viewnumTop3", {destId: this.param.destId}, function (data) {
                vue.travels = data.data;
            })
        }
    },
    filters: {
        dateFormat: function (date) {
            return dateFormat(date, "YYYY-MM-DD")
        }
    },
    mounted: function () {
        this.param = getParams();
        this.queryDetail();
        this.queryToasts();
        this.queryStrViewnumTop3();
        this.queryTravelViewnumTop3();
        this.queryComments();
    }
});

