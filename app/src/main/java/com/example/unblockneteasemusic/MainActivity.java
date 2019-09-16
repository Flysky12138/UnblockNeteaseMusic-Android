package com.example.unblockneteasemusic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootTools.RootTools;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity {

    final String codePath = "/data/data/com.example.unblockneteasemusic/code";
    final String Start = "./node app.js";
    final String State = "[ \"`pgrep node`\" != \"\" ] && echo YES";
    final String Stop = "killall -9 node >/dev/null 2>&1";
    final String ProxyStart = "./proxy.sh start";
    final String ProxyState = "./proxy.sh";
    final String ProxyStop = "./proxy.sh stop";
    final String string = "1、软件运行需要联网！\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //判断手机、APP是否ROOT
        if (RootTools.isRootAvailable()) {
            if (!RootTools.isAccessGiven()) {
                new ToastText(MainActivity.this, "您的App未授权root权限");
                finish();
            }
        } else {
            new ToastText(MainActivity.this, "您的手机未获得root权限");
            finish();
        }
        //复制核心文件文件到/data/data/*/code,权限0777
        Copy copy = new Copy();
        copy.copyFilesAssets(MainActivity.this, "UnblockNeteaseMusic-0.15.1", codePath);
        copy.copyFilesAssets(MainActivity.this, "code", codePath);
        copy.copyFilesAssets(MainActivity.this, "shell", codePath);
        Command command = new Command(0, "cd " + codePath, "chmod 0777 *");
        try {
            RootTools.getShell(true).add(command);
        } catch (IOException | TimeoutException | RootDeniedException e) {
            e.printStackTrace();
        }
        //设置滚动条
        TextView tx1 = findViewById(R.id.tx1);
        TextView tx2 = findViewById(R.id.tx2);
        tx1.setMovementMethod(ScrollingMovementMethod.getInstance());
        tx2.setMovementMethod(ScrollingMovementMethod.getInstance());
        tx1.setText(string);
        //如果State返回YES,则执行Stop Start
        try {
            RootTools.closeAllShells();
        } catch (IOException e) {
            e.printStackTrace();
        }
        command = new Command(0, State) {
            @Override
            public void commandOutput(int id, String line) {
                if (line != "YES") {
                    try {
                        RootTools.closeAllShells();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    TextView tx1 = findViewById(R.id.tx1);
                    tx1.setText("");
                    Command command = new Command(0, Stop, "cd " + codePath, Start) {
                        @Override
                        public void commandOutput(int id, String line) {
                            TextView tx1 = findViewById(R.id.tx1);
                            tx1.append(line + "\n");
                        }
                    };
                    try {
                        RootTools.getShell(true).add(command);
                    } catch (TimeoutException | RootDeniedException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        try {
            RootTools.getShell(true).add(command);
        } catch (TimeoutException | RootDeniedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //清空tx2
        TextView tx2 = findViewById(R.id.tx2);
        tx2.setText("");
        //ProxyState
        try {
            RootTools.closeAllShells();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Command command = new Command(0, "cd " + codePath, ProxyState) {
            @Override
            public void commandOutput(int id, String line) {
                TextView tx2 = findViewById(R.id.tx2);
                tx2.append(line + "\n\n");
            }
        };
        try {
            RootTools.getShell(true).add(command);
        } catch (IOException | TimeoutException | RootDeniedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            RootTools.closeAllShells();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Command command = new Command(0, State) {
            @Override
            public void commandOutput(int id, String line) {
                if (line != "YES") {
                    new ToastText(MainActivity.this, "程序正在后台运行");
                }
            }
        };
        try {
            RootTools.getShell(true).add(command);
        } catch (TimeoutException | RootDeniedException | IOException e) {
            e.printStackTrace();
        }
    }

    //Start按键监听
    public void Start(View view) throws TimeoutException, RootDeniedException, IOException {
        //Stop Start
        TextView tx1 = findViewById(R.id.tx1);
        tx1.setText("");
        RootTools.closeAllShells();
        Command command = new Command(0, Stop, "cd " + codePath, Start) {
            @Override
            public void commandOutput(int id, String line) {
                TextView tx1 = findViewById(R.id.tx1);
                tx1.append(line + "\n");
            }
        };
        RootTools.getShell(true).add(command);
        //ProxyStart
        RootTools.closeAllShells();
        TextView tx2 = findViewById(R.id.tx2);
        tx2.setText("");
        command = new Command(0, "cd " + codePath, ProxyStart) {
            @Override
            public void commandOutput(int id, String line) {
                TextView tx2 = findViewById(R.id.tx2);
                tx2.append(line + "\n\n");
            }
        };
        RootTools.getShell(true).add(command);
    }

    //Stop按键监听
    public void Stop(View view) throws TimeoutException, RootDeniedException, IOException, InterruptedException {
        TextView tx1 = findViewById(R.id.tx1);
        tx1.setText("");
        TextView tx2 = findViewById(R.id.tx2);
        tx2.setText("");
        //Stop ProxyStop
        RootTools.closeAllShells();
        Command command = new Command(0, Stop, "cd " + codePath, ProxyStop) {
            @Override
            public void commandOutput(int id, String line) {
                TextView tx2 = findViewById(R.id.tx2);
                tx2.append(line + "\n");
            }
        };
        RootTools.getShell(true).add(command);
    }
}
