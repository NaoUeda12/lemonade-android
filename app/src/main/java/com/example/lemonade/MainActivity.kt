package com.example.lemonade

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.lemonade.ui.theme.LemonadeTheme

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
            if (showStartButton) {
                // 始めるボタンを表示
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
                                currentStep = 1
                            }
                        )
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
                }
                else if (currentStep == 4) {
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
    LemonadeTheme {
        LemonApp()
    }
}
