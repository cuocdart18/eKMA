package com.app.ekma.common.super_utils.ffmpeg

import kotlin.math.round

/**
 * generate command để tạo video từ list ảnh
 */
fun generateEncodeVideoScript(
    listImage: List<String>,
    musicPath: String?,
    isHaveEffect: Boolean = false,
    outputPath: String,
    mTimePerFrame: Double
): String {
    var command = ""
    listImage.forEach {
        command += "-loop 1 -t $mTimePerFrame -i $it "   //set time for image with -t
    }
    musicPath?.let {
        command += "-i $it "    //set music
    }
    command += "-filter_complex \""

    val baseRatio =
        "scale=720x1280:force_original_aspect_ratio=decrease,setdar=9/16,pad=720:1280:-1:-1,setsar=1" //set quality and ratio
    for (i in listImage.indices) {
        if (isHaveEffect) {
            //set effect transition
            command += when (i) {
                0 -> {
                    "[$i:v]$baseRatio,fade=t=out:st=2.5:d=0.5[stream$i];" // TODO: need change st=2.5 follow timePerFrame
                }

                listImage.size - 1 -> {
                    "[$i:v]$baseRatio,fade=t=in:st=0:d=0.5[stream$i];"
                }

                else -> {
                    "[$i:v]$baseRatio,fade=t=in:st=0:d=0.5,fade=t=out:st=2.5:d=0.5[stream$i];"
                }
            }
        } else {
            command += "[$i:v]$baseRatio[stream$i];"
        }
    }

    for (i in listImage.indices) {
        command += "[stream$i]"
    }
    val listImageSize = listImage.size
    command += "concat=n=$listImageSize:v=1:a=0[v]\" -map $listImageSize:a -map \"[v]\" -shortest -c:v libx264 -preset veryfast -b:v 720k -pix_fmt yuv420p $outputPath"

    return command
}
//        một vài command mẫu chạy tốt - mồ hôi và nước mắt cả đấy :(
//        command = "-y -i $outputFilePath -ss 00:00:02 -t 4 -c:v libx264 -pix_fmt yuv420p -movflags +faststart $outputFilePath2" //work
//        command = "-i $outputFilePath -c copy $outputFilePath2" //work
//        command = "-framerate 1 -i ${root}image%d.jpg -c:v libx264 -pix_fmt yuv420p -s 1080x1920 -movflags +faststart $outputFilePath2" //work
//        command = "-loop 1 -i $photo1Path -c:v libx264 -t 5 -pix_fmt yuv420p -s 1080x1920 -movflags +faststart $outputFilePath2" //work
//        command = "-framerate 1/2 -i $photo1Path -framerate 1/2 -i $photo2Path -framerate 1/2 -i $photo3Path -filter_complex \"[0:v] [1:v] [2:v] concat=n=3:v=1:a=0\" -c:v libx264 -pix_fmt yuv420p -s 1080x1920 -movflags +faststart $outputFilePath2" //work
//        command = "-framerate 1/3 -i $photo1Path -framerate 1/3 -i $photo2Path -framerate 1/3 -i $photo3Path -i $mp3File -filter_complex \"[0:v] [1:v] [2:v] concat=n=3:v=1:a=0\" -map 3:a -shortest -c:v libx264 -pix_fmt yuv420p -s 1080x1920 -movflags +faststart $outputFilePath2" // add mp3 work
//        command = "-framerate 1/3 -i $photo1Path -framerate 1/3 -i $photo2Path -framerate 1/3 -i $photo3Path -i $mp3File -filter_complex \"[0:v]scale=w=min(1080\\,iw):h=min(1080\\,ih):force_original_aspect_ratio=decrease[0v];[1:v]scale=w=min(1080\\,iw):h=min(1080\\,ih):force_original_aspect_ratio=decrease[1v];[2:v]scale=w=min(1080\\,iw):h=min(1080\\,ih):force_original_aspect_ratio=decrease[2v];[0v][1v][2v]concat=n=3:v=1:a=0[v]\" -map 3:a -map \"[v]\" -shortest -c:v libx264 -b:v 5000k -pix_fmt yuv420p -movflags +faststart $outputFilePath2" // update resolution work
//        command = "-framerate 1/3 -i $photo1Path -framerate 1/3 -i $photo2Path -framerate 1/3 -i $photo3Path -i $mp3File -filter_complex \"[0:v]scale=1080x1920:force_original_aspect_ratio=decrease,setdar=9/16,pad=1080:1920:-1:-1,setsar=1[0v];[1:v]scale=1080x1920:force_original_aspect_ratio=decrease,setdar=9/16,pad=1080:1920:-1:-1,setsar=1[1v];[2:v]scale=1080x1920:force_original_aspect_ratio=decrease,setdar=9/16,pad=1080:1920:-1:-1,setsar=1[2v];[0v][1v][2v]concat=n=3:v=1:a=0[v]\" -map 3:a -map \"[v]\" -shortest -c:v libx264 -b:v 5000k -pix_fmt yuv420p -movflags +faststart $outputFilePath2" //work with 1080x1920
//        command = "-framerate 25 -i $photo1Path -framerate 25 -i $photo2Path -framerate 25 -i $photo3Path -i $mp3File -filter_complex \"[0:v]scale=1080x1920:force_original_aspect_ratio=decrease,setdar=9/16,pad=1080:1920:-1:-1,setsar=1,loop=75:1:0[0v];[1:v]scale=1080x1920:force_original_aspect_ratio=decrease,setdar=9/16,pad=1080:1920:-1:-1,setsar=1,loop=75:1:0[1v];[2:v]scale=1080x1920:force_original_aspect_ratio=decrease,setdar=9/16,pad=1080:1920:-1:-1,setsar=1,loop=75:1:0[2v];[0v]fade=t=out:st=2.5:d=0.5[0v];[1v]fade=t=in:st=0:d=0.5,fade=t=out:st=2.5:d=0.5[1v];[2v]fade=t=in:st=0:d=0.5[2v];[0v][1v][2v]concat=n=3:v=1:a=0[v]\" -map 3:a -map \"[v]\" -shortest -c:v libx264 -b:v 5000k -pix_fmt yuv420p -movflags +faststart $outputFilePath2" //work with FADE
//        command = "-loop 1 -t 3 -i $photo1Path -loop 1 -t 3 -i $photo2Path -loop 1 -t 3 -i $photo3Path -i $mp3File -filter_complex \"[0:v]scale=1080x1920:force_original_aspect_ratio=decrease,setdar=9/16,pad=1080:1920:-1:-1,setsar=1[0v];[1:v]scale=1080x1920:force_original_aspect_ratio=decrease,setdar=9/16,pad=1080:1920:-1:-1,setsar=1[1v];[2:v]scale=1080x1920:force_original_aspect_ratio=decrease,setdar=9/16,pad=1080:1920:-1:-1,setsar=1[2v];[0v]fade=t=out:st=2.5:d=0.5[0v];[1v]fade=t=in:st=0:d=0.5,fade=t=out:st=2.5:d=0.5[1v];[2v]fade=t=in:st=0:d=0.5[2v];[0v][1v][2v]concat=n=3:v=1:a=0[v]\" -map 3:a -map \"[v]\" -shortest -c:v libx264 -b:v 5000k -pix_fmt yuv420p -movflags +faststart $outputFilePath2" //work with fade and loop


/**
 * generate command để thêm text vào video
 */
fun addSubtitle(
    listSub: List<String>,
    inputPath: String,
    outputPath: String,
    mTimePerFrame: Double,
    fontPath: String
): String {
    val commandBuilder = StringBuilder("-i $inputPath -vf \"")
    val default = "box=1:boxcolor=Black@0.5:boxborderw=5:fontcolor=white:fontsize=40"
    for (i in listSub.indices) {
        val formattedText = formatText(listSub[i])
        val numberLine = formattedText.count {
            it == '\n'
        }

        val rootStartTime = i * mTimePerFrame
        val rootEndTime = (i + 1) * mTimePerFrame
        val x = "(w-text_w)/2"
        val y =
            "(h*3/4)-text_h" //bởi vì Y của exoPlayer view đã scale lên 1.6, nên tọa độ y để đặt text sẽ được điều chỉnh thành như trên

        if (numberLine <= 1) {
            //case text nhỏ hơn or = 2 dòng
            commandBuilder.append("drawtext=fontfile=$fontPath:text='$formattedText':$default:x='$x':y=$y:enable='between(t,$rootStartTime,$rootEndTime)'")
        } else {
            //case text > 2 dòng, thì sẽ chia ra các lần show nhỏ hơn. ví dụ ban đầu cả cụm là 6 dòng thì sẽ chia thành show 3 lần, mỗi lần 2 dòng

            val listString = formattedText.split("\n")
            val newList = mutableListOf<String>()
            for (item in listString.indices step 2) {
                val newString = if (item + 1 < listString.size) {
                    "${listString[item]}\n${listString[item + 1]}"
                } else {
                    listString[item]
                }
                newList.add(newString)
            } //đoạn này để tách formattedText thành list với item là 2 dòng, item cuối nếu list size lẻ thì là 1 dòng

            val numberShow = newList.size
            val timeEachShow =
                round(100 * mTimePerFrame / numberShow) / 100 // time của từng lượt show được chia đều
            for (index in 0 until numberShow) {
                //show text của từng lượt
                val startTime = rootStartTime + timeEachShow * index
                val endTime = rootStartTime + timeEachShow * (index + 1)
                commandBuilder.append("drawtext=fontfile=$fontPath:text='${newList[index]}':$default:x='$x':y=$y:enable='between(t,$startTime,$endTime)'")
                if (index != numberShow - 1) {
                    commandBuilder.append(",")
                }
            }
        }

        if (i != listSub.size - 1) {
            commandBuilder.append(",")
        } else {
            commandBuilder.append("\" -shortest -c:v libx264 -preset veryfast -b:v 720k -pix_fmt yuv420p $outputPath")
        }
    }
    return commandBuilder.toString()
}

/**
 * Chèn ký tự xuống dòng vào text, 27 là số lượng ký tự tối đa trên 1 dòng ,
 * dựa trên chiều ngang màn hình và độ rộng của font chữ (font size hiện tại = 40).
 * Cần tính toán lại để tối ưu
 */
private fun formatText(text: String): String {
    val newText =
        text.replace("\'", "").replace(":", "") // ffmpeg add text fail nếu chứa ký tự ' và :
    val words = newText.split(" ")
    var lineLength = 0
    var result = ""
    for (word in words) {
        if (lineLength + word.length > 27) {
            result += "\n"
            lineLength = 0
        }
        result += "$word "
        lineLength += word.length + 1
    }
    return result
}