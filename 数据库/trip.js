/*
 Navicat Premium Data Transfer

 Source Server         : mongodb-ubuntu
 Source Server Type    : MongoDB
 Source Server Version : 70005 (7.0.5)
 Source Host           : 192.168.175.134:27017
 Source Schema         : trip

 Target Server Type    : MongoDB
 Target Server Version : 70005 (7.0.5)
 File Encoding         : 65001

 Date: 21/02/2024 12:43:52
*/


// ----------------------------
// Collection structure for strategy_comment
// ----------------------------
db.getCollection("strategy_comment").drop();
db.createCollection("strategy_comment");

// ----------------------------
// Documents of strategy_comment
// ----------------------------
db.getCollection("strategy_comment").insert([ {
    _id: ObjectId("65bbb01a6f618c5594babaa7"),
    strategyId: NumberLong("4"),
    strategyTitle: "吃20cm的油炸大蜈蚣、⑥种昆虫！五毒之首到底什么味道？",
    userId: NumberLong("8"),
    nickname: "user001",
    level: NumberInt("0"),
    headImgUrl: "/images/default.jpg",
    createTime: ISODate("2024-02-01T14:52:09.972Z"),
    content: "咦惹",
    likesnum: NumberInt("0"),
    likesList: [ ],
    _class: "cn.wolfcode.wolf2w.comment.domain.StrategyComment"
} ]);
db.getCollection("strategy_comment").insert([ {
    _id: ObjectId("65bbb0b46f618c5594babaa9"),
    strategyId: NumberLong("17"),
    strategyTitle: "成都周边一日游，去都江堰和青城山吸氧避霾",
    userId: NumberLong("8"),
    nickname: "user001",
    level: NumberInt("0"),
    headImgUrl: "/images/default.jpg",
    createTime: ISODate("2024-02-01T14:54:44.401Z"),
    content: "可以的",
    likesnum: NumberInt("1"),
    likesList: [
        NumberLong("8")
    ],
    _class: "cn.wolfcode.wolf2w.comment.domain.StrategyComment"
} ]);
db.getCollection("strategy_comment").insert([ {
    _id: ObjectId("65bc94f2044f117ecdd21b80"),
    strategyId: NumberLong("14"),
    strategyTitle: "日本好吃不贵的排队美食大全!（含东京、大阪、京都）",
    userId: NumberLong("8"),
    nickname: "user001",
    level: NumberInt("0"),
    headImgUrl: "/images/default.jpg",
    createTime: ISODate("2024-02-02T07:08:34.102Z"),
    content: "看饿了",
    likesnum: NumberInt("1"),
    likesList: [
        NumberLong("8")
    ],
    _class: "cn.wolfcode.wolf2w.comment.domain.StrategyComment"
} ]);
db.getCollection("strategy_comment").insert([ {
    _id: ObjectId("65be3b97bfd8e57509c9a207"),
    strategyId: NumberLong("12"),
    strategyTitle: "京城十大胡同｜不当一回胡同串子，你算白来北京了 ！",
    userId: NumberLong("8"),
    nickname: "user001",
    level: NumberInt("0"),
    headImgUrl: "/images/default.jpg",
    createTime: ISODate("2024-02-03T13:11:50.996Z"),
    content: "不错",
    likesnum: NumberInt("0"),
    likesList: [ ],
    _class: "cn.wolfcode.wolf2w.comment.domain.StrategyComment"
} ]);

// ----------------------------
// Collection structure for travel_comment
// ----------------------------
db.getCollection("travel_comment").drop();
db.createCollection("travel_comment");

// ----------------------------
// Documents of travel_comment
// ----------------------------
db.getCollection("travel_comment").insert([ {
    _id: ObjectId("65bbb09e6f618c5594babaa8"),
    travelId: NumberLong("6"),
    travelTitle: "上海海昌海洋公园",
    userId: NumberLong("8"),
    nickname: "user001",
    level: NumberInt("0"),
    headImgUrl: "/images/default.jpg",
    type: NumberInt("0"),
    createTime: ISODate("2024-02-01T14:54:22.484Z"),
    content: "不错",
    refComment: { },
    _class: "cn.wolfcode.wolf2w.comment.domain.TravelComment"
} ]);
db.getCollection("travel_comment").insert([ {
    _id: ObjectId("65bc91c46f618c5594babaaa"),
    travelId: NumberLong("5"),
    travelTitle: "5月趣上海，非去不可",
    userId: NumberLong("8"),
    nickname: "user001",
    level: NumberInt("0"),
    headImgUrl: "/images/default.jpg",
    type: NumberInt("0"),
    createTime: ISODate("2024-02-02T06:54:59.973Z"),
    content: "asd",
    refComment: { },
    _class: "cn.wolfcode.wolf2w.comment.domain.TravelComment"
} ]);

// ----------------------------
// Collection structure for user_strategy_favorite
// ----------------------------
db.getCollection("user_strategy_favorite").drop();
db.createCollection("user_strategy_favorite");

// ----------------------------
// Documents of user_strategy_favorite
// ----------------------------
db.getCollection("user_strategy_favorite").insert([ {
    _id: "8",
    favoriteList: [
        NumberLong("4")
    ],
    _class: "cn.wolfcode.wolf2w.user.domain.UserStrategyFavorite"
} ]);
db.getCollection("user_strategy_favorite").insert([ {
    _id: "1",
    favoriteList: [ ],
    _class: "cn.wolfcode.wolf2w.user.domain.UserStrategyFavorite"
} ]);
db.getCollection("user_strategy_favorite").insert([ {
    _id: "2",
    favoriteList: [
        NumberLong("5")
    ],
    _class: "cn.wolfcode.wolf2w.user.domain.UserStrategyFavorite"
} ]);

// ----------------------------
// Collection structure for user_travel_favorite
// ----------------------------
db.getCollection("user_travel_favorite").drop();
db.createCollection("user_travel_favorite");

// ----------------------------
// Documents of user_travel_favorite
// ----------------------------
db.getCollection("user_travel_favorite").insert([ {
    _id: "8",
    favoriteList: [ ],
    _class: "cn.wolfcode.wolf2w.user.domain.UserTravelFavorite"
} ]);
db.getCollection("user_travel_favorite").insert([ {
    _id: "1",
    favoriteList: [
        NumberLong("2")
    ],
    _class: "cn.wolfcode.wolf2w.user.domain.UserTravelFavorite"
} ]);
db.getCollection("user_travel_favorite").insert([ {
    _id: "2",
    favoriteList: [ ],
    _class: "cn.wolfcode.wolf2w.user.domain.UserTravelFavorite"
} ]);
