package com.example.lemonade

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import com.example.lemonade.ui.theme.LemonadeTheme
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LemonadeTheme {
                LemonApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun LemonApp() {
    var currentStep by remember { mutableStateOf(0) } // 0に初期化
    var squeezeCount by remember { mutableStateOf(0) }
    var showCompletionDialog by remember { mutableStateOf(false) } // ポップアップ表示フラグ
    var showStartButton by remember { mutableStateOf(true) } // 始めるボタンの表示フラグ

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Lemonade",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = colorResource(id = R.color.yellow)
                )
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.tertiaryContainer),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize(), // サイズを親に合わせる
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showStartButton) {
                    LemonImageOnStartScreen(imageResorceId = R.drawable.lemon_squeeze)
                    StartButton(onClick = {
                        showStartButton = false // ボタンが押されたら非表示にする
                        currentStep = 1 // 現在のステップを1にする
                    })
                } else {
                    when (currentStep) {
                        1 -> {
                            LemonTextAndImage(
                                textResorcedId = R.string.Lemontree,
                                imageResorceId = R.drawable.lemon_tree,
                                onImageClick = {
                                    currentStep = 2
                                    squeezeCount = (2..4).random() // ランダムな絞る回数を設定
                                }
                            )
                        }

                        2 -> {
                            LemonTextAndImage(
                                textResorcedId = R.string.Lemon,
                                imageResorceId = R.drawable.lemon_squeeze,
                                onImageClick = {
                                    squeezeCount--
                                    if (squeezeCount <= 0) {
                                        currentStep = 3
                                    }
                                },
                                squeezeCount = squeezeCount // squeezeCountを渡す
                            )
                        }

                        3 -> {
                            LemonTextAndImage(
                                textResorcedId = R.string.Glassoflemonade,
                                imageResorceId = R.drawable.lemon_drink,
                                onImageClick = {
                                    showCompletionDialog = true // 完成ポップアップを表示
                                }
                            )
                        }

                        4 -> {
                            LemonTextAndImage(
                                textResorcedId = R.string.Emptyglass,
                                imageResorceId = R.drawable.lemon_restart,
                                onImageClick = {
                                    currentStep = 5
                                }
                            )
                        }

                        5 -> {
                            // ドラッグ可能なテキストの表示
                            Draggablelemon(imageResorceId = R.drawable.lemon_squeeze, otherImageResorceId = R.drawable.lemon_drop)

                            }

                    }

                    // ポップアップダイアログ
                    if (showCompletionDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                showCompletionDialog = false // ポップアップを閉じる
                                currentStep = 4
                            },
                            title = { Text(text = "完成！") },
                            text = { Text(text = "レモネードが完成しました！") },
                            confirmButton = {
                                Button(onClick = {
                                    showCompletionDialog = false // ポップアップを閉じる
                                    currentStep = 4
                                }) {
                                    Text("閉じる")
                                }
                            }
                        )
                    } else if (currentStep == 4) {
                        LemonTextAndImage(
                            textResorcedId = R.string.Emptyglass,
                            imageResorceId = R.drawable.lemon_restart,
                            onImageClick = {
                                currentStep = 1 // 再スタートする場合の処理
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Draggablelemon(imageResorceId: Int, otherImageResorceId: Int) {
    Box(modifier = Modifier.fillMaxSize()) {
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }
        var showLemonJuice by remember { mutableStateOf(false) }
        var lemonJuiceOffsetY by remember { mutableStateOf(0f) } // レモン汁のオフセット

        // レモンの画像をドラッグ可能なボックスの下に配置
        Image(
            painter = painterResource(id = imageResorceId),
            contentDescription = null,
            modifier = Modifier
                .padding(16.dp)
                .padding(top= 80.dp)
                .offset(x = offsetX.roundToInt().dp, y = offsetY.roundToInt().dp) // ドラッグに合わせてオフセット
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            // レモンをタップしたときの処理
                            showLemonJuice = true // レモン汁を表示
                            lemonJuiceOffsetY = 200f // レモン汁のオフセットを設定（落ちる位置）
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
        )

        // レモン汁を表示する要素
        if (showLemonJuice) {
            Image(
                painter = painterResource(id = otherImageResorceId),
                contentDescription = null,
                modifier = Modifier
                    .offset(x = offsetX.roundToInt().dp, y = (offsetY + lemonJuiceOffsetY).roundToInt().dp) // レモン汁の位置をオフセット
                    .size(50.dp) // サイズを調整
            )

            // レモン汁が表示されたら、落ちるアニメーションを追加
            LaunchedEffect(lemonJuiceOffsetY) {
                if (lemonJuiceOffsetY > 0f) {
                    delay(500) // 少し待つ
                    lemonJuiceOffsetY += 10f // 落ちる速度を調整
                    if (lemonJuiceOffsetY > 600f) { // 画面下部に達したら
                        showLemonJuice = false // レモン汁を非表示にする
                        lemonJuiceOffsetY = 0f // オフセットをリセット
                    }
                }
            }
        }
    }
}
@Composable
fun StartButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center // 中央に配置
    ) {
        Button(onClick = onClick) {
            Text("始める") // 始めるボタンのテキスト
        }
    }
}

@Composable
fun LemonImageOnStartScreen(imageResorceId: Int) {
    var isExpanded by remember { mutableStateOf(true) } // 初期は拡大状態

    // アニメーションのスケールを制御
    val scale: Float by animateFloatAsState(
        targetValue = if (isExpanded) 2f else 1f, // 2倍か1倍にスケール
        animationSpec = tween(durationMillis = 4000) // 4秒で変化
    )

    // アニメーションを5秒間繰り返す
    LaunchedEffect(Unit) {
        while (true) {
            isExpanded = !isExpanded // スケールの反転
            delay(5000L)
        }
    }

    // レモン画像の描画
    Image(
        painter = painterResource(id = imageResorceId),
        contentDescription = null,
        modifier = Modifier
            .scale(scale) // アニメーションを適用
            .padding(16.dp)
            .padding(top = 100.dp)
    )
}

@Composable
fun LemonTextAndImage(
    textResorcedId: Int,
    imageResorceId: Int,
    squeezeCount: Int = 0,
    onImageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Button(
                onClick = onImageClick,
                shape = RoundedCornerShape(dimensionResource(R.dimen.button_corner_radius)),
                colors = ButtonDefaults.buttonColors(colorResource(id = R.color.teal_200))
            ) {
                Image(
                    painter = painterResource(imageResorceId),
                    contentDescription = null,
                    modifier = Modifier
                        .width(dimensionResource(R.dimen.button_image_width))
                        .height(dimensionResource(R.dimen.button_image_height))
                        .padding(dimensionResource(R.dimen.button_interior_padding))
                )
            }
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_vertical)))
            Text(
                text = stringResource(textResorcedId),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview
@Composable
fun LemonPreview() {
    LemonApp()
}