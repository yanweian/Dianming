package com.yan.dianming;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static android.widget.Toast.LENGTH_SHORT;
import static com.yan.dianming.BitmapProcess.readPictureDegree;
import static com.yan.dianming.BitmapProcess.rotaingImageView;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView_count, mTextView_info;
    private EditText mEditText;
    private ImageView getImageView;
    private List<Studentdianming> mStudentdianmings;
    private Map<String, Student> mStudentMap;
    private List<Student> mStudents;
    private ListView mListView_main;
    private MyAdapter mMyAdapterstu;
    private SQLiteDatabase mSQLiteDatabase;
    private AlertDialog mAlertDialog_select, mAlertDialog_class, mAlertDialog_bianji;
    private List<String> mListclass;
    private String[] classes;
    private boolean ischanged = false;
    private ImageView imageView, bigimage;
    private Bitmap abitmap = null;
    //点名
    private AlertDialog mAlertDialog_image, mAlertDialog_dianming;
    //文件浏览器
    private AlertDialog mAlertDialog;
    private AlertDialog.Builder builder;
    private View mDialogview;
    private ListView mListView;
    private List<File> mFiles;
    private MyAdapterFile mMyAdapter;
    private TextView mTextView_dirpath;
    private GestureDetector mGestureDetector;
    private static final File rootfile = Environment.getExternalStorageDirectory();
    private boolean flag = true;
    private String week = "第1周";
    private String classno = "第一次课";
    private String setting = "";
    private String touxiangpath = "";

    //搜索部分
    private MyAdaptersearch mMyAdaptersearch;
    private MyAdapterLittle mMyAdapterLittle;
    private ListView mListView_little;

    //适配器标记，点击事件
    private String listviewmethod = "";
    private final String flagdianming = "dianming";
    private final String flagsousuo = "sousuo";
    private RelativeLayout mRelativeLayout;

    private String nowstuno = "";
    String imgPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initAlertDialog();
                mAlertDialog_select.show();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
        init();
//        Toast.makeText(this, touxiangpath, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_import) {
            setting = "import";
            mAlertDialog.show();
            return true;
        } else if (id == R.id.action_setting) {
            setting = "touxiang";
            mAlertDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getsearch(String searchtext) {
        /*//学生表
        db.execSQL("create table if not exists studenttb(student_no text primary key ," +
                "student_name text not null," +
                "student_class text not null," +
                "student_score double null," +
                "bad double not null)");
        //点名表
        db.execSQL("create table if not exists dianmingtb(stu_no text not null," +
                "week text not null," +
                "class text not null," +
                "status text not null," +
                "primary key(stu_no,week,class))");
        //班级表*/
        mStudents.clear();
        String sql = "select student_no,student_name,student_class," +
                "student_score,bad from studenttb " +
                "where  " +
                "(student_no like '%" + searchtext + "%' " +
                "or student_class like '%" + searchtext + "%' " +
                "or student_name like '%" + searchtext + "%') order by bad desc,student_no";
        Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
        if (cursor != null) {
            String[] names = cursor.getColumnNames();
            while (cursor.moveToNext()) {
                String stu_no = cursor.getString(cursor.getColumnIndex(names[0]));
                String stu_name = cursor.getString(cursor.getColumnIndex(names[1]));
                String stu_class = cursor.getString(cursor.getColumnIndex(names[2]));
                double stu_score = cursor.getDouble(cursor.getColumnIndex(names[3]));
                double bad = cursor.getDouble(cursor.getColumnIndex(names[4]));
                Student student = new Student(stu_no, stu_name, stu_class, stu_score, bad);
                mStudents.add(student);
            }
        }
        cursor.close();
    }

    private void init() {
        mStudents = new ArrayList<>();
        mStudentdianmings = new ArrayList<>();
        mStudentMap = new TreeMap<>();
        mSQLiteDatabase = new DBHelper(this).getWritableDatabase();
        /*获取头像路径*//*
        Cursor cursor = mSQLiteDatabase.rawQuery("select *from settingtb where kind='touxiangpath'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                String[] names = cursor.getColumnNames();
                while (cursor.moveToNext()) {
                    touxiangpath = cursor.getString(cursor.getColumnIndex(names[1]));
                }
            }
        }*/
        setTitle("欢迎使用！");
        mFiles = new ArrayList<>();
        mGestureDetector = new GestureDetector(this, new MyGestureListener());
        mListView_main = (ListView) findViewById(R.id.listview);
        mListView_main.setVisibility(View.GONE);
        mTextView_count = (TextView) findViewById(R.id.counttext);
        mTextView_info = (TextView) findViewById(R.id.textinfo);
        mTextView_info.setVisibility(View.VISIBLE);
        mMyAdapterstu = new MyAdapter(this, mStudentdianmings);
        mListView_main.setAdapter(mMyAdapterstu);
        listviewmethod = flagdianming;
        mMyAdapterLittle = new MyAdapterLittle(MainActivity.this, mStudentdianmings);
        mListView_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listviewmethod.equals(flagdianming)) {
                    initdianming(position);
                    mAlertDialog_dianming.show();
                } else if (listviewmethod.equals(flagsousuo)) {
                    if (mRelativeLayout != null) {
                        mRelativeLayout.setVisibility(View.GONE);
                    }
                    mRelativeLayout = (RelativeLayout) view.findViewById(R.id.detaillayout);
                    if (((String) mRelativeLayout.getTag()).equals(mStudents.get(position).getStu_no())) {
                        mRelativeLayout.setVisibility(View.VISIBLE);
                        getdetail(mStudents.get(position));
                        mListView_little = (ListView) view.findViewById(R.id.goodlistview);
                        mListView_little.setAdapter(mMyAdapterLittle);
                        mListView_little.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                initdianming(position);
                                mAlertDialog_dianming.show();
                            }
                        });
                        setlittlelistviewheight(mListView_little);
                    }
                }
            }
        });
        mListView_main.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (listviewmethod.equals(flagsousuo)) {
                    bianjiAlertDialog(position);
                    mAlertDialog_bianji.show();
                }
                return true;
            }
        });
        mMyAdaptersearch = new MyAdaptersearch(this, mStudents);
        mEditText = (EditText) findViewById(R.id.edit_search);
        getImageView = (ImageView) findViewById(R.id.imagesearch);
        getImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String searchtext = mEditText.getText().toString();
                    getsearch(searchtext);
                    setTitle("搜索结果");
                    //令输入框失去焦点，并退出软键盘
                    mEditText.clearFocus();
                    InputMethodManager imm = (InputMethodManager) MainActivity.this
                            .getSystemService(MainActivity.this.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                    mTextView_count.setVisibility(View.VISIBLE);
                    mListView_main.setVisibility(View.VISIBLE);
                    mTextView_count.setText("共" + mStudents.size() + "条记录");
                    mListView_main.setAdapter(mMyAdaptersearch);
                    listviewmethod = flagsousuo;
                    mTextView_info.setVisibility(View.GONE);
                    mListView_main.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "查询失败", Toast.LENGTH_LONG).show();
                }

            }
        });
        fileinitAlertDialog();
        getClasses();
        initAlertDialog();
    }

    /*根据班级获取学生*/
    private void getStudent(List<String> aclasses, String aweek, String aclassno) {
        mStudentMap.clear();
        mStudents.clear();
        mStudentdianmings.clear();
        if (aclasses.size() > 0) {
            setTitle(aweek + " " + aclassno);
            String sql = "select student_no,student_name,student_class," +
                    "student_score,status,bad from studenttb,dianmingtb " +
                    "where stu_no=student_no " +
                    "and week='" + aweek + "' and class='" + aclassno + "' " +
                    "and student_class in (";
            for (int i = 0; i < aclasses.size(); i++) {
                sql += "'" + aclasses.get(i) + "'";
                if (i == aclasses.size() - 1) {
                    sql += ") order by bad desc,student_class,student_no";
                } else {
                    sql += ",";
                }
            }
            Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
            if (cursor != null) {
                int count = cursor.getCount();
                if (count <= 0) {
                    Toast.makeText(this, aweek + " " + aclassno + "还未点名！", Toast.LENGTH_LONG).show();
                }
                String[] names = cursor.getColumnNames();
                while (cursor.moveToNext()) {
                    String stu_no = cursor.getString(cursor.getColumnIndex(names[0]));
                    String stu_name = cursor.getString(cursor.getColumnIndex(names[1]));
                    String stu_class = cursor.getString(cursor.getColumnIndex(names[2]));
                    double stu_score = cursor.getDouble(cursor.getColumnIndex(names[3]));
                    String stu_status = cursor.getString(cursor.getColumnIndex(names[4]));
                    double bad = cursor.getDouble(cursor.getColumnIndex(names[5]));
                    Studentdianming studentdianming = new Studentdianming(stu_no, stu_name, stu_class, stu_score,
                            aweek, aclassno, stu_status, bad);
                    mStudentdianmings.add(studentdianming);
                }
            }
            cursor.close();
        } else {
            Toast.makeText(this, "未选择班级！", Toast.LENGTH_LONG).show();
        }
        mMyAdapterstu.notifyDataSetChanged();
    }

    //手势触发类
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        //右滑返回上一级
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            super.onFling(e1, e2, velocityX, velocityY);
            if (e2.getX() - e1.getX() > 50) {
//                返回上一级菜单
                String parentfilepath = mTextView_dirpath.getText().toString();
                if (!parentfilepath.equals(rootfile.getAbsolutePath())) {
                    List<File> parentfiles = DirUtil.getlastfiles(parentfilepath);
                    mFiles.clear();
                    mFiles.addAll(parentfiles);
                    mMyAdapter.notifyDataSetChanged();
                    mTextView_dirpath.setText(new File(parentfilepath).getParentFile().getAbsolutePath());
                } else {
                    Toast.makeText(MainActivity.this, "此为最上层目录", LENGTH_SHORT).show();
                }
                //关掉返回提示
                flag = false;
            }
            return true;
        }
    }

    private void getClasses() {
        String sql = "select *from banjitb order by class_name";
        Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
        int count = cursor.getCount();
        classes = new String[count];
        int index = 0;
        if (cursor != null) {
            String[] names = cursor.getColumnNames();
            while (cursor.moveToNext()) {
                classes[index++] = cursor.getString(cursor.getColumnIndex(names[0]));
            }
        }
        cursor.close();
    }

    //插入数据到stutb
    private void InsertStudent(Map<String, Student> mapstu) {
        Set<String> set = mapstu.keySet();
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            Student student = mapstu.get(iterator.next());
            ContentValues contentValues = new ContentValues();
            contentValues.put("student_no", student.getStu_no());
            contentValues.put("student_name", student.getStu_name());
            contentValues.put("student_class", student.getStu_class());
            contentValues.put("student_score", student.getScore());
            contentValues.put("bad", student.getBad());
            mSQLiteDatabase.insert("studenttb", null, contentValues);
            contentValues.clear();
            contentValues.put("class_name", student.getStu_class());
            mSQLiteDatabase.insert("banjitb", null, contentValues);
        }
        getClasses();
        initAlertDialog();
    }

    //是否编辑
    private boolean isbianji = false;

    /*初始化编辑信息对话框*/
    private void bianjiAlertDialog(final int position) {
        final Student student = mStudents.get(position);
        builder = new AlertDialog.Builder(MainActivity.this);
        View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.bianjilayout, null);
        final Button button_del = (Button) v.findViewById(R.id.btdel);
        final Button button_bianji = (Button) v.findViewById(R.id.btbianji);
        final Button button_queding = (Button) v.findViewById(R.id.btqueding);
        final EditText editTextxuehao = (EditText) v.findViewById(R.id.editxuehao);
        editTextxuehao.setText(student.getStu_no());
        final EditText editTextxinming = (EditText) v.findViewById(R.id.editxinming);
        editTextxinming.setText(student.getStu_name());
        final EditText editTextbanji = (EditText) v.findViewById(R.id.editbanji);
        editTextbanji.setText(student.getStu_class());
        final ImageView bianjiimageView = (ImageView) v.findViewById(R.id.bianjizhaopian);
        bianjiimageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initimage();
                mAlertDialog_image.show();
            }
        });
        bianjiimageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                /*拍照*/
                nowstuno = student.getStu_no();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//调用android自带的照相机
                imgPath = "/sdcard/test/img.jpg";
                File vFile = new File(imgPath);
                if (!vFile.exists()) {
                    File vDirPath = vFile.getParentFile(); //new File(vFile.getParent());
                    vDirPath.mkdirs();
                }
                Uri uri = Uri.fromFile(vFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//
                startActivityForResult(intent, 1);
                return true;
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                abitmap = getbitmap(student.getStu_no());
                if (abitmap != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bianjiimageView.setImageBitmap(abitmap);
                        }
                    });
                }
            }
        }).start();
        //删除
        button_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setMessage("确定删除" + student.getStu_no() + "（" + student.getStu_name() + "）么？");
                builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //确定删除
                        String sno = student.getStu_no();
                        int re = mSQLiteDatabase.delete("studenttb", "student_no=?", new String[]{sno});
                        int res = mSQLiteDatabase.delete("dianmingtb", "stu_no=?", new String[]{sno});
                        if (re > 0 && res > 0) {
                            Toast.makeText(MainActivity.this, "删除成功！", Toast.LENGTH_LONG).show();
                            mAlertDialog_bianji.dismiss();
                            mStudents.remove(position);
                            mMyAdaptersearch.notifyDataSetChanged();
                            mTextView_count.setText("共" + mStudents.size() + "条记录");
                        }
                    }
                });
                builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog alertDialog = builder1.create();
                alertDialog.show();
            }
        });
        //编辑
        button_bianji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isbianji) {
                    isbianji = !isbianji;
                    button_bianji.setText("保存");
                    editTextxinming.setFocusable(true);
                    editTextxinming.setFocusableInTouchMode(true);
                    editTextbanji.setFocusable(true);
                    editTextbanji.setFocusableInTouchMode(true);
                } else {
                    isbianji = !isbianji;
                    button_bianji.setText("编辑");
                    editTextxinming.setFocusable(false);
                    editTextxinming.setFocusableInTouchMode(false);
                    editTextbanji.setFocusable(false);
                    editTextbanji.setFocusableInTouchMode(false);
                    String sname = editTextxinming.getText().toString();
                    String sbanji = editTextbanji.getText().toString();
                    student.setStu_name(sname);
                    student.setStu_class(sbanji);
                    updatestudent(student);
                    publishstudent(position);
                }
            }
        });
        //确定
        button_queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isbianji) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setMessage("是否保存修改的数据？");
                    builder1.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //保存
                            String sname = editTextxinming.getText().toString();
                            String sbanji = editTextbanji.getText().toString();
                            student.setStu_name(sname);
                            student.setStu_class(sbanji);
                            updatestudent(student);
                            publishstudent(position);
                        }
                    });
                    builder1.setNegativeButton("不保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog alertDialog = builder1.create();
                    alertDialog.show();
                }
                mAlertDialog_bianji.dismiss();
            }
        });
        builder.setView(v);
        mAlertDialog_bianji = builder.create();
    }

    private void updatestudent(Student student) {
        String sno = student.getStu_no();
        String sname = student.getStu_name();
        String sbanji = student.getStu_class();
       /*db.execSQL("create table if not exists studenttb(student_no text primary key ," +
                "student_name text not null," +
                "student_class text not null," +
                "student_score double," +
                "bad double not null," +
                "image blob)");*/
        ContentValues contentValues = new ContentValues();
        contentValues.put("student_name", sname);
        contentValues.put("student_class", sbanji);
        int re = mSQLiteDatabase.update("studenttb", contentValues, "student_no=?", new String[]{sno});
        if (re > 0) {
            Toast.makeText(MainActivity.this, "更新成功！", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "更新失败！", Toast.LENGTH_LONG).show();
        }
    }

    /*初始化文件浏览对话框*/
    private void fileinitAlertDialog() {
        builder = new AlertDialog.Builder(MainActivity.this);
        mDialogview = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialoglayout, null);
        mDialogview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return true;
            }
        });
        mListView = (ListView) mDialogview.findViewById(R.id.dirlistview);
        mTextView_dirpath = (TextView) mDialogview.findViewById(R.id.parent_text);
        mTextView_dirpath.setText(rootfile.getAbsolutePath());
        //初始时为根文件夹rootfile下的子目录
        mFiles = DirUtil.getsubfiles(rootfile);
        mMyAdapter = new MyAdapterFile(mFiles, MainActivity.this);
        mListView.setAdapter(mMyAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File file = mFiles.get(position);
                List<File> subfiles = DirUtil.getsubfiles(file);
                if (subfiles != null) {
                    //将子目录装入mfiles
                    mFiles.clear();
                    mTextView_dirpath.setText(file.getAbsolutePath());
                    if (!subfiles.isEmpty()) {
                        mFiles.addAll(subfiles);
                        if (flag) {
                            Toast.makeText(MainActivity.this, "在目录栏向右滑，可返回上级菜单！", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "此文件夹下无文件！", Toast.LENGTH_LONG).show();
                    }
                    mMyAdapter.notifyDataSetChanged();
                } else {
                    if (setting.equals("touxiang")) {
                        Toast.makeText(MainActivity.this, "这是一个文件！", Toast.LENGTH_LONG).show();
                    } else if (setting.equals("import")) {
                        //如果是个文件
                        String path = file.getAbsolutePath();
                        Map<String, Student> midmap = FileioUtil.getStudents(file);
                        if (midmap == null) {
                            Toast.makeText(MainActivity.this, "请选择具有正确格式的文件！", Toast.LENGTH_LONG).show();
                        } else {
                            mStudents.clear();
                            Set<String> set = midmap.keySet();
                            Iterator<String> iterator = set.iterator();
                            while (iterator.hasNext()) {
                                Student student = midmap.get(iterator.next());
                                mStudents.add(student);
                            }
                            mStudentMap.clear();
                            mStudentMap.putAll(midmap);
                            mAlertDialog.dismiss();
                            Toast.makeText(MainActivity.this, "导入成功！", Toast.LENGTH_LONG).show();
                            //将导入的学生数据导入到数据库中
                            InsertStudent(mStudentMap);
                        }
                        mAlertDialog.dismiss();
                    }
                }
            }
        });
        builder.setView(mDialogview);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                touxiangpath = mTextView_dirpath.getText().toString();
                /*        db.execSQL("create table if not exists settingtb
                (kind text primary key,valuestring text not null)");
                */
                List<File> imagefiles = DirUtil.getsubfiles(new File(touxiangpath));
                if (imagefiles != null) {
                    for (int i = 0; i < imagefiles.size(); i++) {
                        final File file = imagefiles.get(i);
                        //创建线程来处理图像的插入
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    InsertImage(file);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                    Toast.makeText(MainActivity.this, "设置完成!", Toast.LENGTH_LONG).show();
                }
            }
        });
        mAlertDialog = builder.create();
    }

    /*插入学生的头像信息*/
    private void InsertImage(File file) throws IOException {
        //将头像信息保存到数据库中
        /*
        * 1.看数据库中是否有相应学生的信息
        * 2.若有则添加进去
        * 3.若没有，则放弃*/
        String stringfilename = file.getName();
        Log.i("sno", stringfilename);
        String[] strings = stringfilename.split("-");
        if (strings != null && strings.length > 2) {
            final String stuno = strings[1];
            Log.i("sno", stuno);
            //strings[1]是学号
                /*db.execSQL("create table if not exists studenttb(student_no text primary key ," +
                "student_name text not null," +
                "student_class text not null," +
                "student_score double," +
                "bad double not null," +
                "image blob)");*/
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bytes = new byte[1024 * 8];
            ArrayList<Byte> bytesarray = new ArrayList<>();
            int count;
            while ((count = fileInputStream.read(bytes)) != -1) {
                for (int i = 0; i < count; i++) {
                    bytesarray.add(bytes[i]);
                }
            }
            int size = bytesarray.size();
            if (size > 2 * 1024 * 1024) {
                Log.i("sno", stuno + "图片太大,未导入！");
            } else {
                bytes = new byte[size];
                for (int i = 0; i < size; i++) {
                    bytes[i] = bytesarray.get(i);
                }
                ContentValues ContentValues = new ContentValues();
                ContentValues.put("image", bytes);
                int re = mSQLiteDatabase.update("studenttb", ContentValues, "student_no=?", new String[]{stuno});
                if (re > 0) {
                    Log.i("sno", stuno + "图片添加成功！");
                } else {
                    Log.i("sno", stuno + "图片添加失败！");
                }
            }
        }
    }

    //初始化点名信息录入弹窗
    private void initAlertDialog() {
        //选择班级的对话框
        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("哪几个班的学生?");
        Log.i("classes", classes.length + "");
        mListclass = new ArrayList<>();
        builder.setMultiChoiceItems(classes, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked)
                    mListclass.add(classes[which]);
                else {
                    mListclass.remove(classes[which]);
                }
            }
        });
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getstartstudent(mListclass, week, classno);
                mListView_main.setAdapter(mMyAdapterstu);
                listviewmethod = flagdianming;
                mTextView_info.setVisibility(View.GONE);
                mListView_main.setVisibility(View.VISIBLE);
                mTextView_count.setVisibility(View.VISIBLE);
                mTextView_count.setText("共" + mStudentdianmings.size() + "条记录");
            }
        });
        mAlertDialog_class = builder.create();

        //选择周数及课次的对话框
        final String[] weeks = new String[18];
        for (int i = 0; i < 18; i++) {
            weeks[i] = "第" + (i + 1) + "周";
        }
        final String[] classnos = new String[2];
        classnos[0] = "第一次课";
        classnos[1] = "第二次课";
        builder = new AlertDialog.Builder(MainActivity.this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialogchooselayout, null);
        final Spinner spinnerweek = (Spinner) view.findViewById(R.id.spinner);
        final Spinner spinnerclassno = (Spinner) view.findViewById(R.id.spinner1);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, weeks);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, classnos);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerweek.setAdapter(arrayAdapter);
        spinnerclassno.setAdapter(arrayAdapter1);
        builder.setView(view);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                week = spinnerweek.getSelectedItem().toString();
                classno = spinnerclassno.getSelectedItem().toString();
                mAlertDialog_class.show();
            }
        });
        mAlertDialog_select = builder.create();
    }


    /*初始化点名信息*/
    private void getstartstudent(List<String> aclasses, String aweek, String aclassno) {
        mStudentMap.clear();
        mStudents.clear();
        mStudentdianmings.clear();
        /*1.先取出对应班级的学生
        * 2.默认全勤，插入到数据库中
        * 3.再从数据库中取出对应班级，周数，课次的信息
        * 4.放入到list中*/
        if (aclasses.size() > 0) {
            setTitle(aweek + " " + aclassno);

            String sql = "select *from studenttb " +
                    "where student_class in (";
            for (int i = 0; i < aclasses.size(); i++) {
                sql += "'" + aclasses.get(i) + "'";
                if (i == aclasses.size() - 1) {
                    sql += ") order by student_class,student_no";
                } else {
                    sql += ",";
                }
            }
            Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
            if (cursor != null) {
                int count = cursor.getCount();
                while (cursor.moveToNext()) {
                    String stu_no = cursor.getString(cursor.getColumnIndex("student_no"));
                    //插入到dianmingtb中
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("stu_no", stu_no);
                    contentValues.put("week", aweek);
                    contentValues.put("class", aclassno);
                    contentValues.put("status", "全勤");
                    mSQLiteDatabase.insert("dianmingtb", null, contentValues);
                }
            }
            cursor.close();

            sql = "select student_no,student_name,student_class," +
                    "student_score,status,bad from studenttb,dianmingtb " +
                    "where stu_no=student_no " +
                    "and week='" + aweek + "' and class='" + aclassno + "' " +
                    "and student_class in (";
            for (int i = 0; i < aclasses.size(); i++) {
                sql += "'" + aclasses.get(i) + "'";
                if (i == aclasses.size() - 1) {
                    sql += ") order by bad desc,student_class,student_no";
                } else {
                    sql += ",";
                }
            }
            cursor = mSQLiteDatabase.rawQuery(sql, null);
            if (cursor != null) {
                String[] names = cursor.getColumnNames();
                while (cursor.moveToNext()) {
                    String stu_no = cursor.getString(cursor.getColumnIndex(names[0]));
                    String stu_name = cursor.getString(cursor.getColumnIndex(names[1]));
                    String stu_class = cursor.getString(cursor.getColumnIndex(names[2]));
                    double stu_score = cursor.getDouble(cursor.getColumnIndex(names[3]));
                    String stu_status = cursor.getString(cursor.getColumnIndex(names[4]));
                    double bad = cursor.getDouble(cursor.getColumnIndex(names[5]));
                    Studentdianming studentdianming = new Studentdianming(stu_no, stu_name, stu_class, stu_score,
                            aweek, aclassno, stu_status, bad);
                    mStudentdianmings.add(studentdianming);
                }
            }
            cursor.close();
        } else {
            Toast.makeText(this, "未选择班级！", Toast.LENGTH_LONG).show();
        }
        mMyAdapterstu.notifyDataSetChanged();
    }

    private void InsertImage(Bitmap bitmap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "正在处理图像...", Toast.LENGTH_LONG).show();
            }
        });
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, arrayOutputStream);
        while (arrayOutputStream.size() > 2 * 1024 * 1024) {
            bitmap = BitmapProcess.ratio(bitmap, 480f, 640f);
            arrayOutputStream.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, arrayOutputStream);
        }
        Log.i("sno", arrayOutputStream.size() + "大小");
        ContentValues ContentValues = new ContentValues();
        ContentValues.put("image", arrayOutputStream.toByteArray());
        int re = mSQLiteDatabase.update("studenttb", ContentValues, "student_no=?", new String[]{nowstuno});
        if (re > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "图片添加成功！", Toast.LENGTH_LONG).show();
                }
            });
            Log.i("sno", nowstuno + "图片添加成功！");
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "图片添加失败！", Toast.LENGTH_LONG).show();
                }
            });
            Log.i("sno", nowstuno + "图片添加失败！");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            abitmap = BitmapFactory.decodeFile(imgPath, null);
            /**
             * 把图片旋转为正的方向
             */
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int degree = readPictureDegree(imgPath);
                    abitmap = rotaingImageView(degree, abitmap);
                    InsertImage(abitmap);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(abitmap);
                        }
                    });
                }
            }).start();

        }
    }

    private Bitmap getbitmap(String sno) {
        //此处为获取照片的代码
                /*从数据库中取出图片*/
        String sql = "select image from studenttb where student_no='" + sno + "'";
        Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
        int count = cursor.getCount();
        Bitmap bitmap = null;
        Log.i("sno", "查询出的学生记录" + count);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                byte[] bytes = cursor.getBlob(cursor.getColumnIndex("image"));
                Log.i("sno", "图片是否为null" + (bytes == null));
                if (bytes != null) {
                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                }
            }
        }
        cursor.close();
        return bitmap;
    }

    /*点名弹窗的初始化*/
    private void initdianming(final int position) {
        builder = new AlertDialog.Builder(MainActivity.this);
        final Studentdianming studentdianming = mStudentdianmings.get(position);
        View view = LayoutInflater.from(this).inflate(R.layout.dianmingdialoglayout, null);
        TextView textView_stuno = (TextView) view.findViewById(R.id.xuehao);
        TextView textView_stuname = (TextView) view.findViewById(R.id.xinming);
        TextView textView_stuclass = (TextView) view.findViewById(R.id.banji);
        RadioGroup radiogroup = (RadioGroup) view.findViewById(R.id.radio);
        textView_stuno.setText(studentdianming.getStu_no());
        textView_stuname.setText(studentdianming.getStu_name());
        textView_stuclass.setText(studentdianming.getStu_class());
        imageView = (ImageView) view.findViewById(R.id.zhaopian);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initimage();
                mAlertDialog_image.show();
            }
        });
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                /*拍照*/
                nowstuno = studentdianming.getStu_no();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//调用android自带的照相机
                imgPath = "/sdcard/test/img.jpg";
                File vFile = new File(imgPath);
                if (!vFile.exists()) {
                    File vDirPath = vFile.getParentFile(); //new File(vFile.getParent());
                    vDirPath.mkdirs();
                }
                Uri uri = Uri.fromFile(vFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//
                startActivityForResult(intent, 1);
                return true;
            }
        });
        abitmap = null;
        new Thread(new Runnable() {
            @Override
            public void run() {
                //此处为获取照片的代码
                /*从数据库中取出图片*/
                abitmap = getbitmap(studentdianming.getStu_no());
                if (abitmap != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(abitmap);
                        }
                    });
                }
            }
        }).start();
        int index = 0;
        int[] radiobuttonindexs = new int[]{R.id.status1, R.id.status2, R.id.status3, R.id.status4, R.id.status5};
        switch (studentdianming.getStatus()) {
            case "全勤":
                index = 0;
                break;
            case "迟到":
                index = 1;
                break;
            case "早退":
                index = 2;
                break;
            case "旷课":
                index = 3;
                break;
            case "请假":
                index = 4;
                break;
        }
        RadioButton radioButton = (RadioButton) view.findViewById(radiobuttonindexs[index]);
        radioButton.setChecked(true);
        final String oldstatus = studentdianming.getStatus();
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ischanged = true;
                switch (checkedId) {
                    case R.id.status1:
                        studentdianming.setStatus("全勤");
                        break;
                    case R.id.status2:
                        studentdianming.setStatus("迟到");
                        break;
                    case R.id.status3:
                        studentdianming.setStatus("早退");
                        break;
                    case R.id.status4:
                        studentdianming.setStatus("旷课");
                        break;
                    case R.id.status5:
                        studentdianming.setStatus("请假");
                        break;
                }
            }
        });
        builder.setView(view);
        builder.setNegativeButton("下一个", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ischanged) {
                    ischanged = false;
                    String sta = studentdianming.getStatus();
                    double badcount = studentdianming.getBadcount();
                    if ((oldstatus.equals("全勤") || oldstatus.equals("请假")) && (!sta.equals("全勤") && !sta.equals("请假"))) {
                        badcount = badcount + 1;
                    } else if ((!oldstatus.equals("全勤") && !oldstatus.equals("请假")) && (sta.equals("全勤") || sta.equals("请假"))) {
                        badcount = badcount - 1;
                    }
                    if (listviewmethod.equals(flagsousuo)) {
                        String stuno = studentdianming.getStu_no();
                        int index = mStudents.indexOf(new Student(stuno, "", "", 0, 0));
                        Student student = mStudents.get(index);
                        student.setBad(badcount);
                    }
                    studentdianming.setBadcount(badcount);
                    //更新数据库
                    updatedianming(studentdianming);
                    publishprogress(position);
                }
                int newposition = position;
                newposition++;
                if (newposition == mStudentdianmings.size()) {
                    Toast.makeText(MainActivity.this, "点名完成!", Toast.LENGTH_LONG).show();
                } else {
                    initdianming(newposition);
                    mAlertDialog_dianming.show();
                }
            }
        });
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ischanged) {
                    ischanged = false;
                    //更新数据库
                    String sta = studentdianming.getStatus();
                    double badcount = studentdianming.getBadcount();
                    if ((oldstatus.equals("全勤") || oldstatus.equals("请假")) && (!sta.equals("全勤") && !sta.equals("请假"))) {
                        badcount = badcount + 1;
                    } else if ((!oldstatus.equals("全勤") && !oldstatus.equals("请假")) && (sta.equals("全勤") || sta.equals("请假"))) {
                        badcount = badcount - 1;
                    }
                    if (listviewmethod.equals(flagsousuo)) {
                        String stuno = studentdianming.getStu_no();
                        int index = mStudents.indexOf(new Student(stuno, "", "", 0, 0));
                        Student student = mStudents.get(index);
                        student.setBad(badcount);
                    }
                    studentdianming.setBadcount(badcount);
                    updatedianming(studentdianming);
                    publishprogress(position);
                }
            }
        });

        mAlertDialog_dianming = builder.create();
    }

    private void initimage() {
        builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.imagedialoglayout, null);
        builder.setView(view);
        bigimage = (ImageView) view.findViewById(R.id.bigimage);
        if (abitmap != null) {
            bigimage.setImageBitmap(abitmap);
        }
        mAlertDialog_image = builder.create();
    }

    private void updatedianming(Studentdianming studentdianming) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", studentdianming.getStatus());
        ContentValues contentValuesa = new ContentValues();
        contentValuesa.put("bad", studentdianming.getBadcount());
        /*cSQL("create table if not exists dianmingtb(stu_no text not null," +
                "week text not null," +
                "  text not null," +
                "status text not null," +
                "primary key(stu_no,week,class))");*/
        /*create table if not exists studenttb(student_no text primary key ," +
                "student_name text not null," +
                "student_class text not null," +
                "student_score double null," +
                "bad double not null)");*/
        try {
            int counta = mSQLiteDatabase.update("studenttb", contentValuesa, "student_no=?",
                    new String[]{studentdianming.getStu_no()});
            int count = mSQLiteDatabase.update("dianmingtb", contentValues,
                    " stu_no=? and week=? and class=? ",
                    new String[]{studentdianming.getStu_no(), studentdianming.getWeek(),
                            studentdianming.getClassno()});
            if (count > 0 && counta > 0) {
                Toast.makeText(this, "更新成功！", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "更新失败，数据库错误！", Toast.LENGTH_LONG).show();
        }
    }

    private void publishstudent(int position) {
        //只有当视图在可见区域的时候才改变
        if (position >= mListView_main.getFirstVisiblePosition() && position <= mListView_main.getLastVisiblePosition()) {
            int positionInlistview = position - mListView_main.getFirstVisiblePosition();
            View view = mListView_main.getChildAt(positionInlistview);
            Student mItem = mStudents.get(position);
            TextView textView = (TextView) view.findViewById(R.id.goodstu_class);
            TextView textViewcount = (TextView) view.findViewById(R.id.goodstu_name);
            textView.setText(mItem.getStu_class());
            textViewcount.setText(mItem.getStu_name() + "");
        }
    }

    public void publishprogress(int position) {
        //只有当视图在可见区域的时候才改变
        if (listviewmethod.equals(flagdianming) && position >= mListView_main.getFirstVisiblePosition() && position <= mListView_main.getLastVisiblePosition()) {
            int positionInlistview = position - mListView_main.getFirstVisiblePosition();
            View view = mListView_main.getChildAt(positionInlistview);
            Studentdianming mItem = mStudentdianmings.get(position);
            TextView textView = (TextView) view.findViewById(R.id.stu_status);
            TextView textViewcount = (TextView) view.findViewById(R.id.badcount);
            textView.setText(mItem.getStatus());
            textViewcount.setText(mItem.getBadcount() + "");
            if (mItem.getBadcount() < 1) {
                textViewcount.setTextColor(Color.parseColor("green"));
            } else if (mItem.getBadcount() < 3) {
                textViewcount.setTextColor(Color.parseColor("#c4c111"));
            } else {
                textViewcount.setTextColor(Color.parseColor("red"));
            }
            if (mItem.getStatus().equals("旷课")) {
                textView.setTextColor(Color.parseColor("red"));
            } else if (mItem.getStatus().equals("全勤")) {
                textView.setTextColor(Color.parseColor("green"));
            } else {
                textView.setTextColor(Color.parseColor("#c4c111"));
            }
        } else if (listviewmethod.equals(flagsousuo) && position >= mListView_little.getFirstVisiblePosition() && position <= mListView_little.getLastVisiblePosition()) {
            int positionInlistview = position - mListView_little.getFirstVisiblePosition();
            View view = mListView_little.getChildAt(positionInlistview);
            Studentdianming mItem = mStudentdianmings.get(position);
            TextView textView = (TextView) view.findViewById(R.id.littlestatus);
            textView.setText(mItem.getStatus());
            if (mItem.getStatus().equals("旷课")) {
                textView.setTextColor(Color.parseColor("red"));
            } else if (mItem.getStatus().equals("全勤")) {
                textView.setTextColor(Color.parseColor("green"));
            } else {
                textView.setTextColor(Color.parseColor("#c4c111"));
            }

            String stuno = mItem.getStu_no();
            int index = mStudents.indexOf(new Student(stuno, "", "", 0, 0));
            Student student = mStudents.get(index);
            int positionInlistviewa = index - mListView_main.getFirstVisiblePosition();
            View viewa = mListView_main.getChildAt(positionInlistviewa);
            TextView textViewcount = (TextView) viewa.findViewById(R.id.goodbadcount);
            textViewcount.setText(student.getBad() + "");
            if (student.getBad() < 1) {
                textViewcount.setTextColor(Color.parseColor("green"));
            } else if (student.getBad() < 3) {
                textViewcount.setTextColor(Color.parseColor("#c4c111"));
            } else {
                textViewcount.setTextColor(Color.parseColor("red"));
            }
        }
    }

    private void getdetail(Student student) {
        mStudentdianmings.clear();
        String sql = "select student_no,student_name,student_class," +
                "student_score,status,bad,week,class from studenttb,dianmingtb " +
                "where stu_no=student_no " +
                "and student_no='" + student.getStu_no() + "' order by week,class";
        Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
        if (cursor != null) {
            String[] names = cursor.getColumnNames();
            while (cursor.moveToNext()) {
                String stu_no = cursor.getString(cursor.getColumnIndex(names[0]));
                String stu_name = cursor.getString(cursor.getColumnIndex(names[1]));
                String stu_class = cursor.getString(cursor.getColumnIndex(names[2]));
                double stu_score = cursor.getDouble(cursor.getColumnIndex(names[3]));
                String stu_status = cursor.getString(cursor.getColumnIndex(names[4]));
                double bad = cursor.getDouble(cursor.getColumnIndex(names[5]));
                String aweek = cursor.getString(cursor.getColumnIndex(names[6]));
                String aclassno = cursor.getString(cursor.getColumnIndex(names[7]));
                Studentdianming studentdianming = new Studentdianming(stu_no, stu_name, stu_class, stu_score,
                        aweek, aclassno, stu_status, bad);
                mStudentdianmings.add(studentdianming);
            }
        }
        cursor.close();
    }


    //计算littlelistview的高度
    private void setlittlelistviewheight(ListView listview) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listview.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listview);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listview.getLayoutParams();
        params.height = totalHeight + (listview.getDividerHeight() * (listAdapter.getCount() - 1));
        listview.setLayoutParams(params);
    }

    //重写返回键

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (!mStudentdianmings.isEmpty() || !mStudents.isEmpty()) {
            mStudentdianmings.clear();
            mStudents.clear();
            setTitle("欢迎使用！");
            mListView_main.setVisibility(View.GONE);
            mTextView_info.setVisibility(View.VISIBLE);
            mTextView_count.setVisibility(View.GONE);
        } else {
            finish();
        }
    }
}
