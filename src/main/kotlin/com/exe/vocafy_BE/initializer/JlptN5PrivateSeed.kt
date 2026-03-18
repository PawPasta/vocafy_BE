package com.exe.vocafy_BE.initializer

import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.LanguageSet
import com.exe.vocafy_BE.enum.PartOfSpeech
import com.exe.vocafy_BE.enum.SyllabusSourceType
import com.exe.vocafy_BE.enum.SyllabusVisibility
import com.exe.vocafy_BE.model.entity.Syllabus

object JlptN5PrivateSeed : SyllabusSeedModule {
    override val key: String = "jlpt-n5-private"

    override fun seed(context: SyllabusSeedContext): Syllabus = with(context) {
        val privateSyllabus = syllabusRepository.save(
            Syllabus(
                title = "JLPT N5 Private Practice",
                description = "Private syllabus for guided practice.",
                imageBackGroud = "https://jlptsensei.com/jlpt-n5-particles-list/",
                imageIcon = "https://www.vjlink.edu.vn/nhung-dieu-ban-nen-biet-ve-tieng-nhat-n5/",
                totalDays = 21,
                languageSet = LanguageSet.EN_JP_VI,
                studyLanguage = LanguageCode.JA,
                visibility = SyllabusVisibility.PRIVATE,
                sourceType = SyllabusSourceType.CURATED,
                createdBy = owner,
                active = true,
                category = generalCategory
            )
        )

        val privateTopicSeeds = listOf(
            TopicSeed(
                title = "Numbers",
                description = "Counting and basic numbers.",
                totalDays = 5,
                courses = listOf(
                    CourseSeed(
                        title = "Counting 1-10",
                        description = "Basic numbers.",
                        vocabularies = listOf(
                            VocabSeed("一", "いち", "one", "one", PartOfSpeech.NOUN),
                            VocabSeed("二", "に", "two", "two", PartOfSpeech.NOUN),
                            VocabSeed("三", "さん", "three", "three", PartOfSpeech.NOUN),
                            VocabSeed("四", "よん", "four", "four", PartOfSpeech.NOUN),
                            VocabSeed("五", "ご", "five", "five", PartOfSpeech.NOUN),
                            VocabSeed("六", "ろく", "six", "six", PartOfSpeech.NOUN),
                            VocabSeed("七", "なな", "seven", "seven", PartOfSpeech.NOUN),
                            VocabSeed("八", "はち", "eight", "eight", PartOfSpeech.NOUN),
                            VocabSeed("九", "きゅう", "nine", "nine", PartOfSpeech.NOUN),
                            VocabSeed("十", "じゅう", "ten", "ten", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Counters",
                        description = "Common counters.",
                        vocabularies = listOf(
                            VocabSeed("一つ", "ひとつ", "one (thing)", "one item", PartOfSpeech.NOUN),
                            VocabSeed("二つ", "ふたつ", "two (things)", "two items", PartOfSpeech.NOUN),
                            VocabSeed("三つ", "みっつ", "three (things)", "three items", PartOfSpeech.NOUN),
                            VocabSeed("四つ", "よっつ", "four (things)", "four items", PartOfSpeech.NOUN),
                            VocabSeed("五つ", "いつつ", "five (things)", "five items", PartOfSpeech.NOUN),
                            VocabSeed("六つ", "むっつ", "six (things)", "six items", PartOfSpeech.NOUN),
                            VocabSeed("七つ", "ななつ", "seven (things)", "seven items", PartOfSpeech.NOUN),
                            VocabSeed("八つ", "やっつ", "eight (things)", "eight items", PartOfSpeech.NOUN),
                            VocabSeed("九つ", "ここのつ", "nine (things)", "nine items", PartOfSpeech.NOUN),
                            VocabSeed("十", "とお", "ten (things)", "ten items", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Money",
                        description = "Prices and money.",
                        vocabularies = listOf(
                            VocabSeed("円", "えん", "yen", "yen", PartOfSpeech.NOUN),
                            VocabSeed("百", "ひゃく", "hundred", "hundred", PartOfSpeech.NOUN),
                            VocabSeed("千", "せん", "thousand", "thousand", PartOfSpeech.NOUN),
                            VocabSeed("万", "まん", "ten thousand", "ten thousand", PartOfSpeech.NOUN),
                            VocabSeed("安い", "やすい", "cheap", "cheap", PartOfSpeech.ADJ),
                            VocabSeed("高い", "たかい", "expensive", "expensive", PartOfSpeech.ADJ),
                            VocabSeed("買う", "かう", "buy", "buy", PartOfSpeech.VERB),
                            VocabSeed("売る", "うる", "sell", "sell", PartOfSpeech.VERB),
                            VocabSeed("お金", "おかね", "money", "money", PartOfSpeech.NOUN),
                            VocabSeed("値段", "ねだん", "price", "price", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Time",
                description = "Days, dates, and time.",
                totalDays = 7,
                courses = listOf(
                    CourseSeed(
                        title = "Days of the Week",
                        description = "Days of the week.",
                        vocabularies = listOf(
                            VocabSeed("月曜日", "げつようび", "Monday", "Monday", PartOfSpeech.NOUN),
                            VocabSeed("火曜日", "かようび", "Tuesday", "Tuesday", PartOfSpeech.NOUN),
                            VocabSeed("水曜日", "すいようび", "Wednesday", "Wednesday", PartOfSpeech.NOUN),
                            VocabSeed("木曜日", "もくようび", "Thursday", "Thursday", PartOfSpeech.NOUN),
                            VocabSeed("金曜日", "きんようび", "Friday", "Friday", PartOfSpeech.NOUN),
                            VocabSeed("土曜日", "どようび", "Saturday", "Saturday", PartOfSpeech.NOUN),
                            VocabSeed("日曜日", "にちようび", "Sunday", "Sunday", PartOfSpeech.NOUN),
                            VocabSeed("今日", "きょう", "today", "today", PartOfSpeech.NOUN),
                            VocabSeed("明日", "あした", "tomorrow", "tomorrow", PartOfSpeech.NOUN),
                            VocabSeed("昨日", "きのう", "yesterday", "yesterday", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Time of Day",
                        description = "Time expressions.",
                        vocabularies = listOf(
                            VocabSeed("時", "じ", "o'clock", "o'clock", PartOfSpeech.NOUN),
                            VocabSeed("分", "ふん", "minute", "minute", PartOfSpeech.NOUN),
                            VocabSeed("朝", "あさ", "morning", "morning", PartOfSpeech.NOUN),
                            VocabSeed("昼", "ひる", "noon", "noon", PartOfSpeech.NOUN),
                            VocabSeed("夜", "よる", "night", "night", PartOfSpeech.NOUN),
                            VocabSeed("今", "いま", "now", "now", PartOfSpeech.NOUN),
                            VocabSeed("早い", "はやい", "early", "early", PartOfSpeech.ADJ),
                            VocabSeed("遅い", "おそい", "late", "late", PartOfSpeech.ADJ),
                            VocabSeed("時間", "じかん", "time", "time", PartOfSpeech.NOUN),
                            VocabSeed("週末", "しゅうまつ", "weekend", "weekend", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Schedules",
                        description = "Schedule related terms.",
                        vocabularies = listOf(
                            VocabSeed("予定", "よてい", "schedule", "schedule", PartOfSpeech.NOUN),
                            VocabSeed("会議", "かいぎ", "meeting", "meeting", PartOfSpeech.NOUN),
                            VocabSeed("開始", "かいし", "start", "start", PartOfSpeech.NOUN),
                            VocabSeed("終了", "しゅうりょう", "finish", "finish", PartOfSpeech.NOUN),
                            VocabSeed("遅れる", "おくれる", "be late", "be late", PartOfSpeech.VERB),
                            VocabSeed("間に合う", "まにあう", "be on time", "be on time", PartOfSpeech.VERB),
                            VocabSeed("休み", "やすみ", "holiday", "holiday", PartOfSpeech.NOUN),
                            VocabSeed("平日", "へいじつ", "weekday", "weekday", PartOfSpeech.NOUN),
                            VocabSeed("毎日", "まいにち", "every day", "every day", PartOfSpeech.NOUN),
                            VocabSeed("来週", "らいしゅう", "next week", "next week", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Directions",
                description = "Asking for and giving directions.",
                totalDays = 6,
                courses = listOf(
                    CourseSeed(
                        title = "Basic Directions",
                        description = "Left, right, straight.",
                        vocabularies = listOf(
                            VocabSeed("右", "みぎ", "right", "right", PartOfSpeech.NOUN),
                            VocabSeed("左", "ひだり", "left", "left", PartOfSpeech.NOUN),
                            VocabSeed("まっすぐ", "まっすぐ", "straight", "straight", PartOfSpeech.ADV),
                            VocabSeed("曲がる", "まがる", "turn", "turn", PartOfSpeech.VERB),
                            VocabSeed("近い", "ちかい", "near", "near", PartOfSpeech.ADJ),
                            VocabSeed("遠い", "とおい", "far", "far", PartOfSpeech.ADJ),
                            VocabSeed("道", "みち", "road", "road", PartOfSpeech.NOUN),
                            VocabSeed("角", "かど", "corner", "corner", PartOfSpeech.NOUN),
                            VocabSeed("交差点", "こうさてん", "intersection", "intersection", PartOfSpeech.NOUN),
                            VocabSeed("信号", "しんごう", "traffic light", "traffic light", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Places",
                        description = "Common places.",
                        vocabularies = listOf(
                            VocabSeed("駅", "えき", "station", "station", PartOfSpeech.NOUN),
                            VocabSeed("銀行", "ぎんこう", "bank", "bank", PartOfSpeech.NOUN),
                            VocabSeed("病院", "びょういん", "hospital", "hospital", PartOfSpeech.NOUN),
                            VocabSeed("郵便局", "ゆうびんきょく", "post office", "post office", PartOfSpeech.NOUN),
                            VocabSeed("コンビニ", "こんびに", "convenience store", "convenience store", PartOfSpeech.NOUN),
                            VocabSeed("学校", "がっこう", "school", "school", PartOfSpeech.NOUN),
                            VocabSeed("公園", "こうえん", "park", "park", PartOfSpeech.NOUN),
                            VocabSeed("店", "みせ", "shop", "shop", PartOfSpeech.NOUN),
                            VocabSeed("ホテル", "ほてる", "hotel", "hotel", PartOfSpeech.NOUN),
                            VocabSeed("トイレ", "といれ", "toilet", "toilet", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Asking for Help",
                        description = "Phrases for asking directions.",
                        vocabularies = listOf(
                            VocabSeed("ここ", "ここ", "here", "here", PartOfSpeech.PRON),
                            VocabSeed("そこ", "そこ", "there", "there", PartOfSpeech.PRON),
                            VocabSeed("あそこ", "あそこ", "over there", "over there", PartOfSpeech.PRON),
                            VocabSeed("どちら", "どちら", "which way", "which way", PartOfSpeech.PRON),
                            VocabSeed("教える", "おしえる", "teach/tell", "tell", PartOfSpeech.VERB),
                            VocabSeed("聞く", "きく", "ask", "ask", PartOfSpeech.VERB),
                            VocabSeed("助ける", "たすける", "help", "help", PartOfSpeech.VERB),
                            VocabSeed("迷う", "まよう", "get lost", "get lost", PartOfSpeech.VERB),
                            VocabSeed("地図", "ちず", "map", "map", PartOfSpeech.NOUN),
                            VocabSeed("案内", "あんない", "guide", "guide", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
        )

        seedSyllabusContent(privateSyllabus, privateTopicSeeds)

        return privateSyllabus
    }
}
