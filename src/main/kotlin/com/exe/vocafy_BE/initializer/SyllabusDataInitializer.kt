package com.exe.vocafy_BE.initializer

import com.exe.vocafy_BE.enum.EnrollmentStatus
import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.LanguageSet
import com.exe.vocafy_BE.enum.LearningState
import com.exe.vocafy_BE.enum.MediaType
import com.exe.vocafy_BE.enum.PartOfSpeech
import com.exe.vocafy_BE.enum.Role
import com.exe.vocafy_BE.enum.ScriptType
import com.exe.vocafy_BE.enum.SyllabusSourceType
import com.exe.vocafy_BE.enum.SyllabusVisibility
import com.exe.vocafy_BE.enum.VocabularyQuestionType
import com.exe.vocafy_BE.model.entity.Course
import com.exe.vocafy_BE.model.entity.CourseVocabularyLink
import com.exe.vocafy_BE.model.entity.Enrollment
import com.exe.vocafy_BE.model.entity.Syllabus
import com.exe.vocafy_BE.model.entity.SyllabusTargetLanguage
import com.exe.vocafy_BE.model.entity.SyllabusTopicLink
import com.exe.vocafy_BE.model.entity.Topic
import com.exe.vocafy_BE.model.entity.TopicCourseLink
import com.exe.vocafy_BE.model.entity.UserVocabProgress
import com.exe.vocafy_BE.model.entity.Vocabulary
import com.exe.vocafy_BE.model.entity.VocabularyExample
import com.exe.vocafy_BE.model.entity.VocabularyExampleTranslation
import com.exe.vocafy_BE.model.entity.VocabularyMeaning
import com.exe.vocafy_BE.model.entity.VocabularyMedia
import com.exe.vocafy_BE.model.entity.VocabularyQuestion
import com.exe.vocafy_BE.model.entity.VocabularyTerm
import com.exe.vocafy_BE.repo.CategoryRepository
import com.exe.vocafy_BE.repo.CourseRepository
import com.exe.vocafy_BE.repo.CourseVocabularyLinkRepository
import com.exe.vocafy_BE.repo.EnrollmentRepository
import com.exe.vocafy_BE.repo.SyllabusRepository
import com.exe.vocafy_BE.repo.SyllabusTargetLanguageRepository
import com.exe.vocafy_BE.repo.SyllabusTopicLinkRepository
import com.exe.vocafy_BE.repo.TopicRepository
import com.exe.vocafy_BE.repo.TopicCourseLinkRepository
import com.exe.vocafy_BE.repo.UserRepository
import com.exe.vocafy_BE.repo.UserVocabProgressRepository
import com.exe.vocafy_BE.repo.VocabularyExampleRepository
import com.exe.vocafy_BE.repo.VocabularyExampleTranslationRepository
import com.exe.vocafy_BE.repo.VocabularyMeaningRepository
import com.exe.vocafy_BE.repo.VocabularyMediaRepository
import com.exe.vocafy_BE.repo.VocabularyQuestionRepository
import com.exe.vocafy_BE.repo.VocabularyRepository
import com.exe.vocafy_BE.repo.VocabularyTermRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import java.time.LocalDate

@Configuration
class SyllabusDataInitializer {

    @Bean
    @Order(2)
    fun seedSyllabusData(
        syllabusRepository: SyllabusRepository,
        courseRepository: CourseRepository,
        vocabularyRepository: VocabularyRepository,
        topicRepository: TopicRepository,
        userRepository: UserRepository,
        syllabusTargetLanguageRepository: SyllabusTargetLanguageRepository,
        vocabularyTermRepository: VocabularyTermRepository,
        vocabularyMeaningRepository: VocabularyMeaningRepository,
        vocabularyExampleRepository: VocabularyExampleRepository,
        vocabularyExampleTranslationRepository: VocabularyExampleTranslationRepository,
        vocabularyMediaRepository: VocabularyMediaRepository,
        vocabularyQuestionRepository: VocabularyQuestionRepository,
        categoryRepository: CategoryRepository,
        syllabusTopicLinkRepository: SyllabusTopicLinkRepository,
        topicCourseLinkRepository: TopicCourseLinkRepository,
        courseVocabularyLinkRepository: CourseVocabularyLinkRepository,
        userVocabProgressRepository: UserVocabProgressRepository,
        enrollmentRepository: EnrollmentRepository,
    ) = ApplicationRunner {
        val n5VietnameseMeaningByJa = mapOf(
            "こんにちは" to "xin chào",
            "さようなら" to "tạm biệt",
            "おはよう" to "chào buổi sáng",
            "こんばんは" to "chào buổi tối",
            "ありがとう" to "cảm ơn",
            "すみません" to "xin lỗi",
            "はい" to "vâng",
            "いいえ" to "không",
            "またね" to "hẹn gặp lại",
            "はじめまして" to "rất vui được gặp",
            "私" to "tôi",
            "名前" to "tên",
            "学生" to "học sinh",
            "先生" to "giáo viên",
            "会社員" to "nhân viên công ty",
            "出身" to "quê quán",
            "日本" to "Nhật Bản",
            "ベトナム" to "Việt Nam",
            "です" to "là",
            "よろしく" to "mong được giúp đỡ",
            "お願いします" to "làm ơn",
            "どうぞ" to "xin mời",
            "失礼します" to "xin phép",
            "大丈夫" to "ổn",
            "ちょっと" to "một chút",
            "今" to "bây giờ",
            "後で" to "lát nữa",
            "早く" to "nhanh",
            "ゆっくり" to "chậm rãi",
            "少し" to "một ít",
            "何" to "cái gì",
            "誰" to "ai",
            "どこ" to "ở đâu",
            "いつ" to "khi nào",
            "なぜ" to "tại sao",
            "どう" to "như thế nào",
            "どれ" to "cái nào",
            "いくら" to "bao nhiêu tiền",
            "いくつ" to "bao nhiêu cái",
            "どんな" to "loại nào",
            "起きる" to "thức dậy",
            "朝" to "buổi sáng",
            "顔" to "mặt",
            "洗う" to "rửa",
            "朝ご飯" to "bữa sáng",
            "コーヒー" to "cà phê",
            "水" to "nước",
            "新聞" to "báo",
            "読む" to "đọc",
            "出かける" to "ra ngoài",
            "仕事" to "công việc",
            "会議" to "cuộc họp",
            "資料" to "tài liệu",
            "メール" to "email",
            "送る" to "gửi",
            "電話" to "điện thoại",
            "話す" to "nói",
            "休む" to "nghỉ ngơi",
            "昼ご飯" to "bữa trưa",
            "忙しい" to "bận",
            "帰る" to "về",
            "晩ご飯" to "bữa tối",
            "料理" to "nấu ăn",
            "食べる" to "ăn",
            "風呂" to "bồn tắm",
            "入る" to "vào",
            "テレビ" to "tivi",
            "見る" to "xem",
            "寝る" to "ngủ",
            "夜" to "đêm",
            "家" to "nhà",
            "部屋" to "phòng",
            "掃除" to "dọn dẹp",
            "掃除する" to "dọn dẹp",
            "洗濯" to "giặt giũ",
            "洗濯する" to "giặt quần áo",
            "料理する" to "nấu ăn",
            "買い物" to "mua sắm",
            "買う" to "mua",
            "空港" to "sân bay",
            "飛行機" to "máy bay",
            "切符" to "vé",
            "荷物" to "hành lý",
            "パスポート" to "hộ chiếu",
            "出発" to "khởi hành",
            "到着" to "đến nơi",
            "搭乗口" to "cổng lên máy bay",
            "待つ" to "chờ",
            "時間" to "thời gian",
            "駅" to "nhà ga",
            "電車" to "tàu điện",
            "バス" to "xe buýt",
            "地図" to "bản đồ",
            "道" to "đường",
            "右" to "bên phải",
            "左" to "bên trái",
            "近い" to "gần",
            "遠い" to "xa",
            "行く" to "đi",
            "レストラン" to "nhà hàng",
            "メニュー" to "thực đơn",
            "注文" to "gọi món",
            "肉" to "thịt",
            "魚" to "cá",
            "野菜" to "rau",
            "美味しい" to "ngon",
            "辛い" to "cay",
            "払う" to "trả tiền",
            "店" to "cửa hàng",
            "値段" to "giá",
            "高い" to "đắt",
            "安い" to "rẻ",
            "これ" to "cái này",
            "それ" to "cái đó",
            "サイズ" to "kích cỡ",
            "試着" to "thử đồ",
            "レジ" to "quầy thanh toán",
            "ご飯" to "cơm",
            "パン" to "bánh mì",
            "牛乳" to "sữa",
            "茶" to "trà",
            "砂糖" to "đường",
            "塩" to "muối",
            "甘い" to "ngọt",
            "苦い" to "đắng",
            "熱い" to "nóng",
            "冷たい" to "lạnh",
            "お茶" to "trà",
            "ジュース" to "nước ép",
            "氷" to "đá",
            "多い" to "nhiều",
            "おかわり" to "gọi thêm",
            "カップ" to "cốc",
            "グラス" to "ly",
            "りんご" to "táo",
            "みかん" to "quýt",
            "バナナ" to "chuối",
            "いちご" to "dâu tây",
            "お菓子" to "bánh kẹo",
            "チョコ" to "sô cô la",
            "アイス" to "kem",
            "好き" to "thích",
            "嫌い" to "ghét",
        )

        val n5JapaneseExampleByJa = mapOf(
            "こんにちは" to "朝、先生に「こんにちは」と言います。",
            "さようなら" to "授業の後で「さようなら」とあいさつします。",
            "おはよう" to "友だちに「おはよう」と言いました。",
            "こんばんは" to "夜に近所の人へ「こんばんは」と言います。",
            "ありがとう" to "手伝ってくれて「ありがとう」と伝えました。",
            "すみません" to "電車で人にぶつかって「すみません」と言いました。",
            "はい" to "先生の質問に「はい」と答えます。",
            "いいえ" to "分からないときは「いいえ」と言います。",
            "またね" to "別れるときに「またね」と言いました。",
            "はじめまして" to "自己紹介で「はじめまして」と言います。",
            "私" to "私は日本語を勉強しています。",
            "名前" to "あなたの名前を教えてください。",
            "学生" to "弟は大学の学生です。",
            "先生" to "日本語の先生は親切です。",
            "会社員" to "父は会社員として働いています。",
            "出身" to "私はハノイ出身です。",
            "日本" to "いつか日本へ旅行したいです。",
            "ベトナム" to "ベトナムの料理が大好きです。",
            "です" to "これは私のノートです。",
            "よろしく" to "これからよろしくお願いします。",
            "お願いします" to "もう一度お願いします。",
            "どうぞ" to "席にどうぞ。",
            "失礼します" to "部屋に入る前に失礼しますと言います。",
            "大丈夫" to "心配しないで、大丈夫です。",
            "ちょっと" to "ちょっと待ってください。",
            "今" to "今、宿題をしています。",
            "後で" to "後で電話します。",
            "早く" to "早く学校へ行きます。",
            "ゆっくり" to "ゆっくり話してください。",
            "少し" to "少し日本語が話せます。",
            "何" to "これは何ですか。",
            "誰" to "あの人は誰ですか。",
            "どこ" to "駅はどこですか。",
            "いつ" to "試験はいつですか。",
            "なぜ" to "なぜ日本語を勉強しますか。",
            "どう" to "今日は気分はどうですか。",
            "どれ" to "この中でどれが好きですか。",
            "いくら" to "この本はいくらですか。",
            "いくつ" to "りんごはいくつありますか。",
            "どんな" to "どんな音楽を聞きますか。",
            "起きる" to "毎日6時に起きる。",
            "朝" to "朝はコーヒーを飲みます。",
            "顔" to "朝に顔を洗います。",
            "洗う" to "食べる前に手を洗う。",
            "朝ご飯" to "毎日朝ご飯を食べます。",
            "コーヒー" to "朝はコーヒーを一杯飲みます。",
            "水" to "運動の後で水を飲みます。",
            "新聞" to "父は毎朝新聞を読みます。",
            "読む" to "寝る前に本を読む。",
            "出かける" to "午後に友だちと出かける。",
            "仕事" to "今日は仕事が多いです。",
            "会議" to "午後3時から会議があります。",
            "資料" to "会議の資料を準備します。",
            "メール" to "先生にメールを送りました。",
            "送る" to "友だちに写真を送る。",
            "電話" to "母に電話をかけます。",
            "話す" to "日本語でゆっくり話す。",
            "休む" to "疲れたので少し休む。",
            "昼ご飯" to "昼ご飯はカレーを食べました。",
            "忙しい" to "今日はとても忙しいです。",
            "帰る" to "授業の後で家に帰る。",
            "晩ご飯" to "家族と晩ご飯を食べます。",
            "料理" to "週末に料理をします。",
            "食べる" to "毎朝パンを食べる。",
            "風呂" to "夜に風呂に入ります。",
            "入る" to "この店に入ってもいいですか。",
            "テレビ" to "夜はテレビを見ます。",
            "見る" to "映画を見るのが好きです。",
            "寝る" to "毎日11時に寝る。",
            "夜" to "夜は静かに勉強します。",
            "家" to "今日は早く家へ帰ります。",
            "部屋" to "部屋をきれいに掃除します。",
            "掃除" to "土曜日に部屋の掃除をします。",
            "掃除する" to "毎朝机を掃除する。",
            "洗濯" to "日曜日に洗濯をします。",
            "洗濯する" to "シャツを洗濯する。",
            "料理する" to "今日は家で料理する。",
            "買い物" to "週末にスーパーで買い物します。",
            "買う" to "新しいノートを買う。",
            "空港" to "空港で友だちを待ちます。",
            "飛行機" to "飛行機で東京へ行きます。",
            "切符" to "駅で切符を買いました。",
            "荷物" to "荷物が重いです。",
            "パスポート" to "旅行の前にパスポートを確認します。",
            "出発" to "明日の朝7時に出発します。",
            "到着" to "飛行機は9時に到着します。",
            "搭乗口" to "搭乗口は10番です。",
            "待つ" to "駅でバスを待つ。",
            "時間" to "時間がないので急ぎます。",
            "駅" to "駅で友だちと会います。",
            "電車" to "毎日電車で通学します。",
            "バス" to "学校までバスで行きます。",
            "地図" to "地図を見て道を探します。",
            "道" to "この道をまっすぐ行ってください。",
            "右" to "次の角を右へ曲がってください。",
            "左" to "信号で左に曲がります。",
            "近い" to "駅はここから近いです。",
            "遠い" to "学校は家から遠いです。",
            "行く" to "明日京都へ行く。",
            "レストラン" to "駅前のレストランで食べます。",
            "メニュー" to "メニューを見せてください。",
            "注文" to "店員さんに注文します。",
            "肉" to "私は肉より魚が好きです。",
            "魚" to "今日は魚を焼きます。",
            "野菜" to "毎日野菜を食べる。",
            "美味しい" to "このラーメンは美味しいです。",
            "辛い" to "このカレーは少し辛いです。",
            "払う" to "レジでお金を払う。",
            "店" to "この店は安くて便利です。",
            "値段" to "この靴の値段はいくらですか。",
            "高い" to "このバッグは高いです。",
            "安い" to "この店の果物は安いです。",
            "これ" to "これをください。",
            "それ" to "それは私のペンです。",
            "サイズ" to "この服のサイズはMです。",
            "試着" to "このシャツを試着してもいいですか。",
            "レジ" to "レジはあちらです。",
            "ご飯" to "ご飯をもう一杯ください。",
            "パン" to "朝はパンと牛乳を食べます。",
            "牛乳" to "冷たい牛乳を飲みます。",
            "茶" to "食後に温かい茶を飲みます。",
            "砂糖" to "コーヒーに砂糖を入れます。",
            "塩" to "スープに少し塩を入れてください。",
            "甘い" to "このケーキはとても甘いです。",
            "苦い" to "この薬は少し苦いです。",
            "熱い" to "このスープはまだ熱いです。",
            "冷たい" to "冷たい水をください。",
            "お茶" to "お茶を一杯お願いします。",
            "ジュース" to "子どもはジュースが好きです。",
            "氷" to "ジュースに氷を入れてください。",
            "多い" to "今日は宿題が多いです。",
            "おかわり" to "ご飯をおかわりしてください。",
            "カップ" to "白いカップを使います。",
            "グラス" to "水をグラスに入れます。",
            "りんご" to "朝にりんごを食べます。",
            "みかん" to "冬はみかんが美味しいです。",
            "バナナ" to "バナナを二本買いました。",
            "いちご" to "いちごケーキを食べたいです。",
            "お菓子" to "子どもにお菓子をあげます。",
            "チョコ" to "このチョコは甘くて美味しいです。",
            "アイス" to "夏はアイスをよく食べます。",
            "好き" to "私は猫が好きです。",
            "嫌い" to "私は辛い食べ物が嫌いです。",
        )

        data class ExampleTranslation(
            val en: String,
            val vi: String,
        )

        val n5ExampleTranslationByJa = mapOf(
            "こんにちは" to ExampleTranslation(
                en = "In the morning, I say \"hello\" to my teacher.",
                vi = "Buổi sáng, tôi nói \"xin chào\" với giáo viên.",
            ),
            "さようなら" to ExampleTranslation(
                en = "After class, I say \"goodbye\" as a greeting.",
                vi = "Sau giờ học, tôi chào tạm biệt bằng câu \"tạm biệt\".",
            ),
            "おはよう" to ExampleTranslation(
                en = "I said \"good morning\" to my friend.",
                vi = "Tôi đã nói \"chào buổi sáng\" với bạn.",
            ),
            "こんばんは" to ExampleTranslation(
                en = "At night, I say \"good evening\" to my neighbor.",
                vi = "Buổi tối, tôi nói \"chào buổi tối\" với hàng xóm.",
            ),
            "ありがとう" to ExampleTranslation(
                en = "I said \"thank you\" for the help.",
                vi = "Tôi nói \"cảm ơn\" vì đã được giúp đỡ.",
            ),
            "すみません" to ExampleTranslation(
                en = "I bumped into someone on the train and said \"excuse me\".",
                vi = "Tôi va vào ai đó trên tàu và nói \"xin lỗi\".",
            ),
            "はい" to ExampleTranslation(
                en = "I answer my teacher's question with \"yes\".",
                vi = "Tôi trả lời câu hỏi của giáo viên bằng \"vâng\".",
            ),
            "いいえ" to ExampleTranslation(
                en = "When I don't understand, I say \"no\".",
                vi = "Khi không hiểu, tôi nói \"không\".",
            ),
            "またね" to ExampleTranslation(
                en = "When parting, I said \"see you later\".",
                vi = "Khi tạm biệt, tôi nói \"hẹn gặp lại\".",
            ),
            "はじめまして" to ExampleTranslation(
                en = "I say \"nice to meet you\" in self-introductions.",
                vi = "Tôi nói \"rất vui được gặp\" khi tự giới thiệu.",
            ),
            "私" to ExampleTranslation(
                en = "I am studying Japanese.",
                vi = "Tôi đang học tiếng Nhật.",
            ),
            "名前" to ExampleTranslation(
                en = "Please tell me your name.",
                vi = "Xin hãy cho tôi biết tên của bạn.",
            ),
            "学生" to ExampleTranslation(
                en = "My younger brother is a university student.",
                vi = "Em trai tôi là sinh viên đại học.",
            ),
            "先生" to ExampleTranslation(
                en = "My Japanese teacher is kind.",
                vi = "Giáo viên tiếng Nhật của tôi rất tốt bụng.",
            ),
            "会社員" to ExampleTranslation(
                en = "My father works as a company employee.",
                vi = "Bố tôi làm nhân viên công ty.",
            ),
            "出身" to ExampleTranslation(
                en = "I am from Hanoi.",
                vi = "Tôi đến từ Hà Nội.",
            ),
            "日本" to ExampleTranslation(
                en = "I want to travel to Japan someday.",
                vi = "Tôi muốn du lịch Nhật Bản một ngày nào đó.",
            ),
            "ベトナム" to ExampleTranslation(
                en = "I really like Vietnamese food.",
                vi = "Tôi rất thích món ăn Việt Nam.",
            ),
            "です" to ExampleTranslation(
                en = "This is my notebook.",
                vi = "Đây là quyển vở của tôi.",
            ),
            "よろしく" to ExampleTranslation(
                en = "Please take care of me from now on.",
                vi = "Mong được bạn giúp đỡ từ bây giờ.",
            ),
            "お願いします" to ExampleTranslation(
                en = "Please say it one more time.",
                vi = "Làm ơn nói lại một lần nữa.",
            ),
            "どうぞ" to ExampleTranslation(
                en = "Please, have a seat.",
                vi = "Xin mời ngồi.",
            ),
            "失礼します" to ExampleTranslation(
                en = "I say \"excuse me\" before entering the room.",
                vi = "Tôi nói \"xin phép\" trước khi vào phòng.",
            ),
            "大丈夫" to ExampleTranslation(
                en = "Don't worry, it's okay.",
                vi = "Đừng lo, ổn mà.",
            ),
            "ちょっと" to ExampleTranslation(
                en = "Please wait a moment.",
                vi = "Vui lòng đợi một chút.",
            ),
            "今" to ExampleTranslation(
                en = "I am doing homework now.",
                vi = "Bây giờ tôi đang làm bài tập.",
            ),
            "後で" to ExampleTranslation(
                en = "I will call you later.",
                vi = "Tôi sẽ gọi cho bạn sau.",
            ),
            "早く" to ExampleTranslation(
                en = "I go to school early.",
                vi = "Tôi đến trường sớm.",
            ),
            "ゆっくり" to ExampleTranslation(
                en = "Please speak slowly.",
                vi = "Vui lòng nói chậm rãi.",
            ),
            "少し" to ExampleTranslation(
                en = "I can speak a little Japanese.",
                vi = "Tôi có thể nói một chút tiếng Nhật.",
            ),
            "何" to ExampleTranslation(
                en = "What is this?",
                vi = "Cái này là gì?",
            ),
            "誰" to ExampleTranslation(
                en = "Who is that person?",
                vi = "Người đó là ai?",
            ),
            "どこ" to ExampleTranslation(
                en = "Where is the station?",
                vi = "Nhà ga ở đâu?",
            ),
            "いつ" to ExampleTranslation(
                en = "When is the exam?",
                vi = "Kỳ thi là khi nào?",
            ),
            "なぜ" to ExampleTranslation(
                en = "Why do you study Japanese?",
                vi = "Tại sao bạn học tiếng Nhật?",
            ),
            "どう" to ExampleTranslation(
                en = "How do you feel today?",
                vi = "Hôm nay bạn cảm thấy thế nào?",
            ),
            "どれ" to ExampleTranslation(
                en = "Which one do you like in this group?",
                vi = "Trong số này bạn thích cái nào?",
            ),
            "いくら" to ExampleTranslation(
                en = "How much is this book?",
                vi = "Quyển sách này giá bao nhiêu?",
            ),
            "いくつ" to ExampleTranslation(
                en = "How many apples are there?",
                vi = "Có bao nhiêu quả táo?",
            ),
            "どんな" to ExampleTranslation(
                en = "What kind of music do you listen to?",
                vi = "Bạn nghe loại nhạc nào?",
            ),
        )

        fun buildLegacyExampleSentence(languageCode: LanguageCode, meaningText: String): String =
            when (languageCode) {
                LanguageCode.EN -> "This word means \"${meaningText}\"."
                LanguageCode.VI -> "Từ này có nghĩa là \"${meaningText}\"."
                LanguageCode.JA -> "この言葉は「${meaningText}」という意味です。"
                LanguageCode.ZH -> "这个词的意思是“${meaningText}”。"
            }

        fun buildLegacyExampleTranslation(languageCode: LanguageCode, meaningText: String): String =
            when (languageCode) {
                LanguageCode.EN -> "Từ này có nghĩa là \"${meaningText}\"."
                LanguageCode.VI -> "This word means \"${meaningText}\"."
                LanguageCode.JA -> "This word means \"${meaningText}\"."
                LanguageCode.ZH -> "This word means \"${meaningText}\"."
            }

        fun buildUsageExampleSentence(
            languageCode: LanguageCode,
            meaningText: String,
            partOfSpeech: PartOfSpeech,
        ): String = when (languageCode) {
            LanguageCode.EN -> when (partOfSpeech) {
                PartOfSpeech.NOUN -> "I use this ${meaningText} every day."
                PartOfSpeech.VERB -> "I ${meaningText} every day."
                PartOfSpeech.ADJ -> "This lesson is very ${meaningText}."
                PartOfSpeech.ADV -> "Please speak ${meaningText} when practicing."
                PartOfSpeech.PRON -> "${meaningText} study Japanese every evening."
                PartOfSpeech.PREP -> "The station is ${meaningText} my house."
                PartOfSpeech.CONJ -> "I was tired, ${meaningText} I kept studying."
                PartOfSpeech.INTERJ -> "${meaningText}! I finally remember this word."
                PartOfSpeech.OTHER -> "I often use \"${meaningText}\" in conversation."
            }
            LanguageCode.VI -> when (partOfSpeech) {
                PartOfSpeech.NOUN -> "Tôi dùng ${meaningText} mỗi ngày."
                PartOfSpeech.VERB -> "Tôi ${meaningText} mỗi ngày."
                PartOfSpeech.ADJ -> "Bài học này rất ${meaningText}."
                PartOfSpeech.ADV -> "Bạn hãy nói ${meaningText} khi luyện tập."
                PartOfSpeech.PRON -> "${meaningText} học tiếng Nhật mỗi tối."
                PartOfSpeech.PREP -> "Trường học ở ${meaningText} nhà ga."
                PartOfSpeech.CONJ -> "Tôi muốn nghỉ, ${meaningText} tôi vẫn tiếp tục học."
                PartOfSpeech.INTERJ -> "${meaningText}! Cuối cùng tôi cũng nhớ từ này."
                PartOfSpeech.OTHER -> "Tôi thường dùng từ \"${meaningText}\" trong hội thoại."
            }
            LanguageCode.JA -> when (partOfSpeech) {
                PartOfSpeech.NOUN -> "私はこの${meaningText}を毎日使います。"
                PartOfSpeech.VERB -> "私は毎日${meaningText}。"
                PartOfSpeech.ADJ -> "このレッスンはとても${meaningText}です。"
                PartOfSpeech.ADV -> "練習するときは${meaningText}話してください。"
                PartOfSpeech.PRON -> "${meaningText}は毎晩日本語を勉強します。"
                PartOfSpeech.PREP -> "学校は駅の${meaningText}にあります。"
                PartOfSpeech.CONJ -> "疲れました。${meaningText}、勉強を続けました。"
                PartOfSpeech.INTERJ -> "${meaningText}！この単語を覚えました。"
                PartOfSpeech.OTHER -> "会話で「${meaningText}」をよく使います。"
            }
            LanguageCode.ZH -> when (partOfSpeech) {
                PartOfSpeech.NOUN -> "我每天都会用这个${meaningText}。"
                PartOfSpeech.VERB -> "我每天都${meaningText}。"
                PartOfSpeech.ADJ -> "这节课很${meaningText}。"
                PartOfSpeech.ADV -> "练习时请${meaningText}地说。"
                PartOfSpeech.PRON -> "${meaningText}每天晚上学日语。"
                PartOfSpeech.PREP -> "学校在车站${meaningText}。"
                PartOfSpeech.CONJ -> "我很累，${meaningText}我还是继续学习。"
                PartOfSpeech.INTERJ -> "${meaningText}！我终于记住这个词了。"
                PartOfSpeech.OTHER -> "我经常在对话里用“${meaningText}”。"
            }
        }

        fun buildUsageExampleTranslation(
            languageCode: LanguageCode,
            meaningText: String,
        ): String = when (languageCode) {
            LanguageCode.EN -> "Câu này dùng từ \"${meaningText}\"."
            LanguageCode.VI -> "This sentence uses the word \"${meaningText}\"."
            LanguageCode.JA -> "This sentence uses the word \"${meaningText}\"."
            LanguageCode.ZH -> "This sentence uses the word \"${meaningText}\"."
        }

        fun buildTermExampleSentence(
            languageCode: LanguageCode,
            termText: String,
            partOfSpeech: PartOfSpeech,
        ): String {
            if (languageCode == LanguageCode.JA) {
                n5JapaneseExampleByJa[termText]?.let { return it }
                return when (partOfSpeech) {
                    PartOfSpeech.VERB -> "私は毎日${termText}。"
                    PartOfSpeech.INTERJ -> "${termText}！今日も頑張りましょう。"
                    else -> "私は「${termText}」を使って文を作ります。"
                }
            }
            return when (languageCode) {
                LanguageCode.EN -> "I use the word \"${termText}\" in a sentence."
                LanguageCode.VI -> "Tôi dùng từ \"${termText}\" trong một câu."
                LanguageCode.ZH -> "我在句子里用“${termText}”。"
                LanguageCode.JA -> "私は「${termText}」を使って文を作ります。"
            }
        }

        fun buildExampleTranslationForJaTerm(
            jaTermText: String,
            targetLanguage: LanguageCode,
            targetTermText: String,
            partOfSpeech: PartOfSpeech,
        ): String {
            val manual = n5ExampleTranslationByJa[jaTermText]
            if (manual != null) {
                return when (targetLanguage) {
                    LanguageCode.EN -> manual.en
                    LanguageCode.VI -> manual.vi
                    LanguageCode.JA -> n5JapaneseExampleByJa[jaTermText] ?: buildTermExampleSentence(
                        LanguageCode.JA,
                        jaTermText,
                        partOfSpeech,
                    )
                    LanguageCode.ZH -> buildTermExampleSentence(targetLanguage, targetTermText, partOfSpeech)
                }
            }
            return buildTermExampleSentence(targetLanguage, targetTermText, partOfSpeech)
        }

        fun isWeakTranslation(text: String?, termHint: String?): Boolean {
            if (text.isNullOrBlank()) {
                return true
            }
            if (termHint.isNullOrBlank()) {
                return text.trim().length < 8
            }
            return text.trim().equals(termHint.trim(), ignoreCase = true)
        }

        fun normalizeVocabularyExamples() {
            vocabularyRepository.findAll().forEach { vocabulary ->
                val vocabId = vocabulary.id ?: return@forEach
                val existingExamples = vocabularyExampleRepository.findAllByVocabularyIdOrderBySortOrderAscIdAsc(vocabId)
                val terms = vocabularyTermRepository.findAllByVocabularyIdOrderByIdAsc(vocabId)
                val meanings = vocabularyMeaningRepository.findAllByVocabularyIdOrderBySenseOrderAscIdAsc(vocabId)
                if (terms.isEmpty() && meanings.isEmpty()) {
                    return@forEach
                }

                val jaTerm = terms.firstOrNull {
                    it.languageCode == LanguageCode.JA && it.scriptType == ScriptType.KANJI
                } ?: terms.firstOrNull { it.languageCode == LanguageCode.JA }
                val enMeaning = meanings.firstOrNull { it.languageCode == LanguageCode.EN }
                val viMeaning = meanings.firstOrNull { it.languageCode == LanguageCode.VI }
                val defaultPartOfSpeech = enMeaning?.partOfSpeech ?: viMeaning?.partOfSpeech ?: PartOfSpeech.OTHER
                val desiredJaSentence = jaTerm?.let {
                    buildTermExampleSentence(LanguageCode.JA, it.textValue, defaultPartOfSpeech)
                }

                if (existingExamples.isNotEmpty()) {
                    val updates = mutableListOf<VocabularyExample>()
                    val existingJa = existingExamples.firstOrNull { it.languageCode == LanguageCode.JA }
                    if (desiredJaSentence != null) {
                        if (existingJa == null) {
                            updates.add(
                                VocabularyExample(
                                    vocabulary = vocabulary,
                                    languageCode = LanguageCode.JA,
                                    sentenceText = desiredJaSentence,
                                    sortOrder = 1,
                                )
                            )
                        } else if (existingJa.sentenceText != desiredJaSentence) {
                            updates.add(
                                VocabularyExample(
                                    id = existingJa.id,
                                    vocabulary = existingJa.vocabulary,
                                    languageCode = existingJa.languageCode,
                                    sentenceText = desiredJaSentence,
                                    sortOrder = existingJa.sortOrder,
                                    isActive = existingJa.isActive,
                                    createdAt = existingJa.createdAt,
                                    updatedAt = existingJa.updatedAt,
                                )
                            )
                        }
                    }
                    if (updates.isNotEmpty()) {
                        vocabularyExampleRepository.saveAll(updates)
                    }
                    return@forEach
                }

                val examplesToSave = mutableListOf<VocabularyExample>()
                if (desiredJaSentence != null) {
                    examplesToSave.add(
                        VocabularyExample(
                            vocabulary = vocabulary,
                            languageCode = LanguageCode.JA,
                            sentenceText = desiredJaSentence,
                            sortOrder = 1,
                        )
                    )
                }

                if (examplesToSave.isNotEmpty()) {
                    vocabularyExampleRepository.saveAll(examplesToSave)
                }
            }
        }

        fun normalizeVocabularyExampleTranslations() {
            vocabularyRepository.findAll().forEach { vocabulary ->
                val vocabId = vocabulary.id ?: return@forEach
                val examples = vocabularyExampleRepository.findAllByVocabularyIdOrderBySortOrderAscIdAsc(vocabId)
                if (examples.isEmpty()) {
                    return@forEach
                }

                val primaryExample = examples.firstOrNull {
                    it.languageCode == LanguageCode.JA && it.isActive
                } ?: examples.firstOrNull { it.isActive } ?: examples.first()
                val primaryExampleId = primaryExample.id ?: return@forEach
                val existingTranslations = vocabularyExampleTranslationRepository
                    .findAllByVocabularyExampleIdOrderByIdAsc(primaryExampleId)
                val existingByLanguage = existingTranslations.associateBy { it.languageCode }

                val meaningRows = vocabularyMeaningRepository.findAllByVocabularyIdOrderBySenseOrderAscIdAsc(vocabId)
                val enMeaning = meaningRows.firstOrNull { it.languageCode == LanguageCode.EN }
                val viMeaning = meaningRows.firstOrNull { it.languageCode == LanguageCode.VI }
                val terms = vocabularyTermRepository.findAllByVocabularyIdOrderByIdAsc(vocabId)
                val jaTerm = terms.firstOrNull {
                    it.languageCode == LanguageCode.JA && it.scriptType == ScriptType.KANJI
                } ?: terms.firstOrNull { it.languageCode == LanguageCode.JA }
                val enTerm = terms.firstOrNull { it.languageCode == LanguageCode.EN }?.textValue
                    ?: enMeaning?.meaningText
                val viTerm = viMeaning?.meaningText ?: jaTerm?.textValue?.let { n5VietnameseMeaningByJa[it] }
                val partOfSpeech = enMeaning?.partOfSpeech ?: viMeaning?.partOfSpeech ?: PartOfSpeech.OTHER

                val desiredByLanguage = linkedMapOf<LanguageCode, String>()
                val enText = jaTerm?.textValue?.let { jaText ->
                    enTerm?.let { termText ->
                        buildExampleTranslationForJaTerm(
                            jaTermText = jaText,
                            targetLanguage = LanguageCode.EN,
                            targetTermText = termText,
                            partOfSpeech = partOfSpeech,
                        )
                    }
                } ?: enMeaning?.exampleSentence
                    ?: enTerm?.let { buildTermExampleSentence(LanguageCode.EN, it, partOfSpeech) }
                if (!enText.isNullOrBlank()) {
                    desiredByLanguage[LanguageCode.EN] = enText
                }
                val viText = jaTerm?.textValue?.let { jaText ->
                    viTerm?.let { termText ->
                        buildExampleTranslationForJaTerm(
                            jaTermText = jaText,
                            targetLanguage = LanguageCode.VI,
                            targetTermText = termText,
                            partOfSpeech = partOfSpeech,
                        )
                    }
                } ?: viMeaning?.exampleSentence
                    ?: viTerm?.let { buildTermExampleSentence(LanguageCode.VI, it, partOfSpeech) }
                if (!viText.isNullOrBlank()) {
                    desiredByLanguage[LanguageCode.VI] = viText
                }

                val upserts = mutableListOf<VocabularyExampleTranslation>()
                desiredByLanguage.forEach { (languageCode, translationText) ->
                    val existing = existingByLanguage[languageCode]
                    if (existing == null) {
                        upserts.add(
                            VocabularyExampleTranslation(
                                vocabularyExample = primaryExample,
                                languageCode = languageCode,
                                translationText = translationText,
                            )
                        )
                    } else if (
                        existing.translationText != translationText ||
                        isWeakTranslation(
                            existing.translationText,
                            if (languageCode == LanguageCode.EN) enTerm else viTerm,
                        )
                    ) {
                        upserts.add(
                            VocabularyExampleTranslation(
                                id = existing.id,
                                vocabularyExample = existing.vocabularyExample,
                                languageCode = existing.languageCode,
                                translationText = translationText,
                                createdAt = existing.createdAt,
                                updatedAt = existing.updatedAt,
                            )
                        )
                    }
                }
                if (upserts.isNotEmpty()) {
                    vocabularyExampleTranslationRepository.saveAll(upserts)
                }
            }
        }

        fun normalizeMeaningExamples() {
            val updates = vocabularyMeaningRepository.findAll().mapNotNull { row ->
                val legacySentence = buildLegacyExampleSentence(row.languageCode, row.meaningText)
                val legacyTranslation = buildLegacyExampleTranslation(row.languageCode, row.meaningText)
                val generatedSentence = buildUsageExampleSentence(row.languageCode, row.meaningText, row.partOfSpeech)
                val generatedTranslation = buildUsageExampleTranslation(row.languageCode, row.meaningText)
                val sentence = if (
                    row.exampleSentence.isNullOrBlank() ||
                    row.exampleSentence == legacySentence ||
                    !(row.exampleSentence?.contains(row.meaningText, ignoreCase = true) ?: false)
                ) {
                    generatedSentence
                } else {
                    row.exampleSentence
                }
                val translation = if (
                    row.exampleTranslation.isNullOrBlank() ||
                    row.exampleTranslation == legacyTranslation
                ) {
                    generatedTranslation
                } else {
                    row.exampleTranslation
                }
                if (sentence == row.exampleSentence && translation == row.exampleTranslation) {
                    return@mapNotNull null
                }
                VocabularyMeaning(
                    id = row.id,
                    vocabulary = row.vocabulary,
                    languageCode = row.languageCode,
                    meaningText = row.meaningText,
                    exampleSentence = sentence,
                    exampleTranslation = translation,
                    partOfSpeech = row.partOfSpeech,
                    senseOrder = row.senseOrder,
                    createdAt = row.createdAt,
                    updatedAt = row.updatedAt,
                )
            }
            if (updates.isNotEmpty()) {
                vocabularyMeaningRepository.saveAll(updates)
            }
        }

        fun normalizeN5VietnameseMeanings() {
            val n5Syllabus = syllabusRepository.findAll().firstOrNull { it.title == "JLPT N5 Starter" } ?: return
            val syllabusId = n5Syllabus.id ?: return
            val courses = topicCourseLinkRepository.findCoursesBySyllabusId(syllabusId)
            if (courses.isEmpty()) {
                return
            }

            val vocabularies = courses.flatMap { course ->
                val courseId = course.id ?: return@flatMap emptyList()
                courseVocabularyLinkRepository.findVocabulariesByCourseId(courseId)
            }.distinctBy { it.id }

            vocabularies.forEach { vocabulary ->
                val vocabId = vocabulary.id ?: return@forEach
                val terms = vocabularyTermRepository.findAllByVocabularyIdOrderByIdAsc(vocabId)
                val jaTerm = terms.firstOrNull {
                    it.languageCode == LanguageCode.JA && it.scriptType == ScriptType.KANJI
                } ?: terms.firstOrNull { it.languageCode == LanguageCode.JA }
                val viText = jaTerm?.textValue?.let { n5VietnameseMeaningByJa[it] } ?: return@forEach

                val meaningRows = vocabularyMeaningRepository.findAllByVocabularyIdOrderBySenseOrderAscIdAsc(vocabId)
                val viRows = meaningRows.filter { it.languageCode == LanguageCode.VI }
                if (viRows.isEmpty()) {
                    val source = meaningRows.firstOrNull { it.languageCode == LanguageCode.EN }
                        ?: meaningRows.firstOrNull()
                        ?: return@forEach
                    val viSentence = buildUsageExampleSentence(LanguageCode.VI, viText, source.partOfSpeech)
                    val viTranslation = buildUsageExampleTranslation(LanguageCode.VI, viText)
                    vocabularyMeaningRepository.save(
                        VocabularyMeaning(
                            vocabulary = vocabulary,
                            languageCode = LanguageCode.VI,
                            meaningText = viText,
                            exampleSentence = viSentence,
                            exampleTranslation = viTranslation,
                            partOfSpeech = source.partOfSpeech,
                            senseOrder = source.senseOrder ?: 1,
                        )
                    )
                    return@forEach
                }

                viRows.forEach { viRow ->
                    val generatedSentence = buildUsageExampleSentence(LanguageCode.VI, viText, viRow.partOfSpeech)
                    val generatedTranslation = buildUsageExampleTranslation(LanguageCode.VI, viText)
                    val legacySentence = buildLegacyExampleSentence(LanguageCode.VI, viText)
                    val legacyTranslation = buildLegacyExampleTranslation(LanguageCode.VI, viText)
                    val needsSentenceRefresh = viRow.exampleSentence.isNullOrBlank() ||
                        viRow.exampleSentence == legacySentence ||
                        !(viRow.exampleSentence?.contains(viText, ignoreCase = true) ?: false)
                    val needsTranslationRefresh = viRow.exampleTranslation.isNullOrBlank() ||
                        viRow.exampleTranslation == legacyTranslation
                    val viSentence = if (needsSentenceRefresh) generatedSentence else viRow.exampleSentence
                    val viTranslation = if (needsTranslationRefresh) generatedTranslation else viRow.exampleTranslation
                    if (viRow.meaningText != viText || viSentence != viRow.exampleSentence || viTranslation != viRow.exampleTranslation) {
                        vocabularyMeaningRepository.save(
                            VocabularyMeaning(
                                id = viRow.id,
                                vocabulary = viRow.vocabulary,
                                languageCode = viRow.languageCode,
                                meaningText = viText,
                                exampleSentence = viSentence,
                                exampleTranslation = viTranslation,
                                partOfSpeech = viRow.partOfSpeech,
                                senseOrder = viRow.senseOrder,
                                createdAt = viRow.createdAt,
                                updatedAt = viRow.updatedAt,
                            )
                        )
                    }
                }
            }
        }

        fun resolveStudyLanguage(languageSet: LanguageSet): LanguageCode =
            when (languageSet) {
                LanguageSet.EN_JP -> LanguageCode.JA
                LanguageSet.EN_VI -> LanguageCode.VI
                LanguageSet.JP_VI -> LanguageCode.VI
                LanguageSet.EN_JP_VI -> LanguageCode.JA
            }

        fun resolveTargetLanguages(languageSet: LanguageSet, studyLanguage: LanguageCode): List<LanguageCode> =
            when (languageSet) {
                LanguageSet.EN_JP -> listOf(LanguageCode.EN, LanguageCode.JA)
                LanguageSet.EN_VI -> listOf(LanguageCode.EN, LanguageCode.VI)
                LanguageSet.JP_VI -> listOf(LanguageCode.JA, LanguageCode.VI)
                LanguageSet.EN_JP_VI -> listOf(LanguageCode.EN, LanguageCode.JA, LanguageCode.VI)
            }
                .filter { it != studyLanguage }
                .ifEmpty { listOf(LanguageCode.EN) }

        fun ensureSyllabusLanguages(syllabus: Syllabus) {
            val syllabusId = syllabus.id ?: return
            val studyLanguage = syllabus.studyLanguage ?: resolveStudyLanguage(syllabus.languageSet)

            if (syllabus.studyLanguage == null) {
                syllabusRepository.save(
                    Syllabus(
                        id = syllabus.id,
                        title = syllabus.title,
                        description = syllabus.description,
                        imageBackGroud = syllabus.imageBackGroud,
                        imageIcon = syllabus.imageIcon,
                        totalDays = syllabus.totalDays,
                        languageSet = syllabus.languageSet,
                        studyLanguage = studyLanguage,
                        visibility = syllabus.visibility,
                        sourceType = syllabus.sourceType,
                        active = syllabus.active,
                        isDeleted = syllabus.isDeleted,
                        createdBy = syllabus.createdBy,
                        category = syllabus.category,
                        createdAt = syllabus.createdAt,
                        updatedAt = syllabus.updatedAt,
                    )
                )
            }

            val existingTargets = syllabusTargetLanguageRepository.findAllBySyllabusIdOrderByIdAsc(syllabusId)
            if (existingTargets.isNotEmpty()) {
                return
            }

            val defaultTargets = resolveTargetLanguages(syllabus.languageSet, studyLanguage)
            val syllabusRef = syllabusRepository.getReferenceById(syllabusId)
            val targetEntities = defaultTargets.map { languageCode ->
                SyllabusTargetLanguage(
                    syllabus = syllabusRef,
                    languageCode = languageCode,
                )
            }
            if (targetEntities.isNotEmpty()) {
                syllabusTargetLanguageRepository.saveAll(targetEntities)
            }
        }

        syllabusRepository.findAll().forEach { ensureSyllabusLanguages(it) }
        normalizeN5VietnameseMeanings()
        normalizeMeaningExamples()
        normalizeVocabularyExamples()
        normalizeVocabularyExampleTranslations()
        enrollmentRepository.findAll().forEach { enrollment ->
            if (enrollment.preferredTargetLanguage != null) {
                return@forEach
            }
            val syllabusId = enrollment.syllabus.id ?: return@forEach
            val targetLanguages = syllabusTargetLanguageRepository.findAllBySyllabusIdOrderByIdAsc(syllabusId)
                .map { it.languageCode }
            val preferredLanguage = targetLanguages.firstOrNull() ?: LanguageCode.EN
            enrollmentRepository.save(
                Enrollment(
                    id = enrollment.id,
                    user = enrollment.user,
                    syllabus = enrollment.syllabus,
                    startDate = enrollment.startDate,
                    status = enrollment.status,
                    preferredTargetLanguage = preferredLanguage,
                    isFocused = enrollment.isFocused,
                )
            )
        }

        if (
            syllabusRepository.count() > 0 ||
            courseRepository.count() > 0 ||
            vocabularyRepository.count() > 0 ||
            topicRepository.count() > 0 ||
            vocabularyTermRepository.count() > 0 ||
            vocabularyMeaningRepository.count() > 0 ||
            vocabularyMediaRepository.count() > 0 ||
            vocabularyQuestionRepository.count() > 0
        ) {
            return@ApplicationRunner
        }

        val users = userRepository.findAll()
        if (users.isEmpty()) {
            return@ApplicationRunner
        }

        val owner = users.firstOrNull { it.role == Role.ADMIN } ?: users.first()
        val categories = categoryRepository.findAll()
        val generalCategory = categories.find { it.name == "General" }
        val businessCategory = categories.find { it.name == "Business" }
        val techCategory = categories.find { it.name == "Technology" }

        fun seedSyllabusContent(syllabus: Syllabus, topicSeeds: List<TopicSeed>) {
            ensureSyllabusLanguages(syllabus)

            val topics = topicSeeds.mapIndexed { index, seed ->
                Topic(
                    createdBy = owner,
                    title = seed.title,
                    description = seed.description,
                    totalDays = seed.totalDays,
                    sortOrder = index + 1,
                )
            }
            val savedTopics = topicRepository.saveAll(topics)
            val topicLinks = savedTopics.map { topic ->
                SyllabusTopicLink(
                    syllabus = syllabus,
                    topic = topic,
                )
            }
            syllabusTopicLinkRepository.saveAll(topicLinks)

            val courses = mutableListOf<Course>()
            savedTopics.zip(topicSeeds).forEach { (_, seed) ->
                seed.courses.forEachIndexed { index, courseSeed ->
                    courses.add(
                        Course(
                            title = courseSeed.title,
                            description = courseSeed.description,
                            sortOrder = index + 1,
                            createdBy = owner,
                        )
                    )
                }
            }
            val savedCourses = courseRepository.saveAll(courses)
            val courseLinks = mutableListOf<TopicCourseLink>()
            savedTopics.zip(topicSeeds).forEach { (topic, seed) ->
                seed.courses.forEach { _ ->
                    val course = savedCourses[courseLinks.size]
                    courseLinks.add(
                        TopicCourseLink(
                            topic = topic,
                            course = course,
                        )
                    )
                }
            }
            if (courseLinks.isNotEmpty()) {
                topicCourseLinkRepository.saveAll(courseLinks)
            }

            val vocabularies = mutableListOf<Vocabulary>()
            val vocabSeeds = mutableListOf<Pair<VocabSeed, Vocabulary>>()
            savedTopics.zip(topicSeeds).forEach { (_, topicSeed) ->
                topicSeed.courses.forEach { courseSeed ->
                    courseSeed.vocabularies.forEachIndexed { index, vocabSeed ->
                        val vocab = Vocabulary(
                            createdBy = owner,
                            note = null,
                            sortOrder = index + 1,
                        )
                        vocabularies.add(vocab)
                        vocabSeeds.add(vocabSeed to vocab)
                    }
                }
            }
            val savedVocabularies = vocabularyRepository.saveAll(vocabularies)
            val vocabLinks = mutableListOf<CourseVocabularyLink>()
            var courseOffset = 0
            savedTopics.zip(topicSeeds).forEach { (_, topicSeed) ->
                topicSeed.courses.forEach { courseSeed ->
                    val course = savedCourses[courseOffset]
                    courseOffset += 1
                    courseSeed.vocabularies.forEachIndexed { _, _ ->
                        val vocab = savedVocabularies[vocabLinks.size]
                        vocabLinks.add(
                            CourseVocabularyLink(
                                course = course,
                                vocabulary = vocab,
                            )
                        )
                    }
                }
            }
            if (vocabLinks.isNotEmpty()) {
                courseVocabularyLinkRepository.saveAll(vocabLinks)
            }

            val terms = mutableListOf<VocabularyTerm>()
            val meanings = mutableListOf<VocabularyMeaning>()
            val examples = mutableListOf<VocabularyExample>()
            val exampleTranslations = mutableListOf<VocabularyExampleTranslation>()
            val medias = mutableListOf<VocabularyMedia>()
            val questions = mutableListOf<VocabularyQuestion>()
            savedVocabularies.zip(vocabSeeds.map { it.first }).forEach { (vocab, seed) ->
                val enSentence = buildUsageExampleSentence(LanguageCode.EN, seed.meaning, seed.partOfSpeech)
                val viMeaningText = seed.viMeaning ?: n5VietnameseMeaningByJa[seed.jaKanji]
                val jaSentence = seed.exampleJa
                    ?.takeIf { it.isNotBlank() }
                    ?: buildTermExampleSentence(LanguageCode.JA, seed.jaKanji, seed.partOfSpeech)
                val enExampleTranslation = seed.exampleEn
                    ?.takeIf { it.isNotBlank() }
                    ?: buildExampleTranslationForJaTerm(
                        jaTermText = seed.jaKanji,
                        targetLanguage = LanguageCode.EN,
                        targetTermText = seed.en,
                        partOfSpeech = seed.partOfSpeech,
                    )
                terms.add(
                    VocabularyTerm(
                        vocabulary = vocab,
                        languageCode = LanguageCode.JA,
                        scriptType = ScriptType.KANJI,
                        textValue = seed.jaKanji,
                    )
                )
                terms.add(
                    VocabularyTerm(
                        vocabulary = vocab,
                        languageCode = LanguageCode.JA,
                        scriptType = ScriptType.KANA,
                        textValue = seed.jaKana,
                    )
                )
                terms.add(
                    VocabularyTerm(
                        vocabulary = vocab,
                        languageCode = LanguageCode.EN,
                        scriptType = ScriptType.LATIN,
                        textValue = seed.en,
                    )
                )
                meanings.add(
                    VocabularyMeaning(
                        vocabulary = vocab,
                        languageCode = LanguageCode.EN,
                        meaningText = seed.meaning,
                        exampleSentence = enSentence,
                        exampleTranslation = buildUsageExampleTranslation(LanguageCode.EN, seed.meaning),
                        partOfSpeech = seed.partOfSpeech,
                        senseOrder = 1,
                    )
                )
                val jaExample = VocabularyExample(
                    vocabulary = vocab,
                    languageCode = LanguageCode.JA,
                    sentenceText = jaSentence,
                    sortOrder = 1,
                )
                examples.add(jaExample)
                exampleTranslations.add(
                    VocabularyExampleTranslation(
                        vocabularyExample = jaExample,
                        languageCode = LanguageCode.EN,
                        translationText = enExampleTranslation,
                    )
                )
                if (!viMeaningText.isNullOrBlank()) {
                    val viSentence = buildUsageExampleSentence(LanguageCode.VI, viMeaningText, seed.partOfSpeech)
                    val viExampleTranslation = buildExampleTranslationForJaTerm(
                        jaTermText = seed.jaKanji,
                        targetLanguage = LanguageCode.VI,
                        targetTermText = viMeaningText,
                        partOfSpeech = seed.partOfSpeech,
                    )
                    meanings.add(
                        VocabularyMeaning(
                            vocabulary = vocab,
                            languageCode = LanguageCode.VI,
                            meaningText = viMeaningText,
                            exampleSentence = viSentence,
                            exampleTranslation = buildUsageExampleTranslation(LanguageCode.VI, viMeaningText),
                            partOfSpeech = seed.partOfSpeech,
                            senseOrder = 1,
                        )
                    )
                    exampleTranslations.add(
                        VocabularyExampleTranslation(
                            vocabularyExample = jaExample,
                            languageCode = LanguageCode.VI,
                            translationText = viExampleTranslation,
                        )
                    )
                }
                medias.add(
                    VocabularyMedia(
                        vocabulary = vocab,
                        mediaType = MediaType.IMAGE,
                        url = "", // Để trống - thêm link sau
                    )
                )
            }

            vocabularyTermRepository.saveAll(terms)
            vocabularyMeaningRepository.saveAll(meanings)
            vocabularyExampleRepository.saveAll(examples)
            if (exampleTranslations.isNotEmpty()) {
                vocabularyExampleTranslationRepository.saveAll(exampleTranslations)
            }
            vocabularyMediaRepository.saveAll(medias)

            val termByVocab = terms
                .filter { it.id != null && it.vocabulary.id != null }
                .groupBy { it.vocabulary.id }
                .mapValues { it.value.first() }
            val meaningByVocab = meanings
                .filter { it.id != null && it.vocabulary.id != null }
                .groupBy { it.vocabulary.id }
                .mapValues { it.value.first() }
            val mediaByVocab = medias
                .filter { it.id != null && it.vocabulary.id != null }
                .groupBy { it.vocabulary.id }
                .mapValues { it.value.first() }

            savedVocabularies.forEach { vocab ->
                val vocabId = vocab.id ?: return@forEach
                val termRef = termByVocab[vocabId]?.id
                val meaningRef = meaningByVocab[vocabId]?.id
                val mediaRef = mediaByVocab[vocabId]?.id

                if (termRef != null && meaningRef != null && mediaRef != null) {
                    questions.add(
                        VocabularyQuestion(
                            vocabulary = vocab,
                            questionType = VocabularyQuestionType.LISTEN_SELECT_TERM,
                            questionRefId = mediaRef,
                            answerRefId = termRef,
                        )
                    )
                    questions.add(
                        VocabularyQuestion(
                            vocabulary = vocab,
                            questionType = VocabularyQuestionType.LOOK_TERM_SELECT_MEANING,
                            questionRefId = termRef,
                            answerRefId = meaningRef,
                        )
                    )
                    questions.add(
                        VocabularyQuestion(
                            vocabulary = vocab,
                            questionType = VocabularyQuestionType.LOOK_MEANING_INPUT_TERM,
                            questionRefId = meaningRef,
                            answerRefId = termRef,
                        )
                    )
                    questions.add(
                        VocabularyQuestion(
                            vocabulary = vocab,
                            questionType = VocabularyQuestionType.LOOK_IMAGE_SELECT_TERM,
                            questionRefId = mediaRef,
                            answerRefId = termRef,
                        )
                    )
                }
            }

            if (questions.isNotEmpty()) {
                vocabularyQuestionRepository.saveAll(questions)
            }
        }

        val seedContext = SyllabusSeedContext(
            owner = owner,
            generalCategory = generalCategory,
            businessCategory = businessCategory,
            techCategory = techCategory,
            syllabusRepository = syllabusRepository,
            seedSyllabusContent = ::seedSyllabusContent,
        )
        val seededSyllabuses = SyllabusSeedRegistry.modules.associate { module ->
            module.key to module.seed(seedContext)
        }

        val publicSyllabus = seededSyllabuses[JlptN5PublicSeed.key] ?: return@ApplicationRunner

        val testUser = userRepository.findByEmail("khiem1371@gmail.com") ?: return@ApplicationRunner
        val testUserId = testUser.id ?: return@ApplicationRunner
        val targetSyllabusId = publicSyllabus.id ?: return@ApplicationRunner
        if (enrollmentRepository.findByUserIdAndSyllabusId(testUserId, targetSyllabusId) == null) {
            val hasFocused = enrollmentRepository.findByUserIdAndIsFocusedTrue(testUserId) != null
            enrollmentRepository.save(
                Enrollment(
                    user = testUser,
                    syllabus = publicSyllabus,
                    startDate = LocalDate.now(),
                    status = EnrollmentStatus.ACTIVE,
                    preferredTargetLanguage = LanguageCode.EN,
                    isFocused = !hasFocused,
                )
            )
        }

        val courses = topicCourseLinkRepository.findCoursesBySyllabusId(targetSyllabusId)
        val vocabularies = courses.flatMap { course ->
            val courseId = course.id ?: return@flatMap emptyList()
            courseVocabularyLinkRepository.findVocabulariesByCourseId(courseId)
        }.distinctBy { it.id }

        if (vocabularies.isNotEmpty()) {
            val vocabIds = vocabularies.mapNotNull { it.id }
            if (vocabIds.isNotEmpty()) {
                val existing = userVocabProgressRepository
                    .findAllByUserIdAndVocabularyIdIn(testUserId, vocabIds)
                    .associateBy { it.vocabulary.id }
                val toCreate = vocabularies.mapIndexedNotNull { index, vocab ->
                    val vocabId = vocab.id ?: return@mapIndexedNotNull null
                    if (existing.containsKey(vocabId)) return@mapIndexedNotNull null
                    val state = when {
                        index < 10 -> LearningState.INTRODUCED
                        index < 20 -> LearningState.LEARNING
                        else -> LearningState.FAMILIAR
                    }
                    UserVocabProgress(
                        user = testUser,
                        vocabulary = vocab,
                        learningState = state.code,
                        exposureCount = (index % 5) + 1,
                        correctStreak = ((index % 3) + 1).toShort(),
                        wrongStreak = 0,
                    )
                }
                if (toCreate.isNotEmpty()) {
                    userVocabProgressRepository.saveAll(toCreate)
                }
            }
        }

        if (userVocabProgressRepository.count() == 0L) {
            val progressUser = userRepository.findByEmail("khiem1371@gmail.com") ?: return@ApplicationRunner
            val vocabList = vocabularyRepository.findAll().take(30)
            if (vocabList.isNotEmpty()) {
                val progressList = vocabList.mapIndexed { index, vocab ->
                    val state = when {
                        index < 10 -> LearningState.INTRODUCED
                        index < 20 -> LearningState.LEARNING
                        else -> LearningState.FAMILIAR
                    }
                    UserVocabProgress(
                        user = progressUser,
                        vocabulary = vocab,
                        learningState = state.code,
                        exposureCount = (index % 5) + 1,
                        correctStreak = ((index % 3) + 1).toShort(),
                        wrongStreak = 0,
                    )
                }
                userVocabProgressRepository.saveAll(progressList)
            }
        }
    }
}
