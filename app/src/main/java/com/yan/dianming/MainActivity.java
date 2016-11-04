package com.yan.dianming;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static android.widget.Toast.LENGTH_SHORT;

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
    private AlertDialog mAlertDialog_select, mAlertDialog_class, mAlertDialog_method;
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
    private String dianmingmethod = "全点";
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
        Toast.makeText(this, touxiangpath, Toast.LENGTH_LONG).show();
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
    }

    private void init() {
        mStudents = new ArrayList<>();
        mStudentdianmings = new ArrayList<>();
        mStudentMap = new TreeMap<>();
        mSQLiteDatabase = new DBHelper(this).getWritableDatabase();
        /*获取头像路径*/
        Cursor cursor = mSQLiteDatabase.rawQuery("select *from settingtb where kind='touxiangpath'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                String[] names = cursor.getColumnNames();
                while (cursor.moveToNext()) {
                    touxiangpath = cursor.getString(cursor.getColumnIndex(names[1]));
                }
            }
        }
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
                    dianmingmethod = "自由点";
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
                                dianmingmethod = "自由点";
                                initdianming(position);
                                mAlertDialog_dianming.show();
                            }
                        });
                        setlittlelistviewheight(mListView_little);
                    }
                }
            }
        });
        mMyAdaptersearch = new MyAdaptersearch(this, mStudents);
        mEditText = (EditText) findViewById(R.id.edit_search);
        getImageView = (ImageView) findViewById(R.id.imagesearch);
        getImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
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
                }catch (Exception e){
                    Toast.makeText(MainActivity.this,"查询失败",Toast.LENGTH_LONG).show();
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
                boolean f = true;
                if (touxiangpath.equals("")) {
                    f = false;
                }
                touxiangpath = mTextView_dirpath.getText().toString();
                /*        db.execSQL("create table if not exists settingtb
                (kind text primary key,valuestring text not null)");
                */
                ContentValues contentValues = new ContentValues();
                contentValues.put("kind", "touxiangpath");
                contentValues.put("valuestring", touxiangpath);
                if (!f) {
                    mSQLiteDatabase.insert("settingtb", null, contentValues);
                } else {
                    mSQLiteDatabase.update("settingtb", contentValues, "kind='touxiangpath'", null);
                }
                Toast.makeText(MainActivity.this, "设置完成!", Toast.LENGTH_LONG).show();
            }
        });
        mAlertDialog = builder.create();
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
                mAlertDialog_method.show();
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

        builder = new AlertDialog.Builder(this);
        builder.setTitle("怎么点名？");
        final String[] strings = new String[]{"全点", "自由点"};
        dianmingmethod="全点";
        builder.setSingleChoiceItems(strings, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dianmingmethod = strings[which];
            }
        });
        builder.setPositiveButton("开始点名！", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dianmingmethod.equals("全点")) {
                    initdianming(0);
                    mAlertDialog_dianming.show();
                }
            }
        });
        mAlertDialog_method = builder.create();
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
                if (count <= 0) {
                    Toast.makeText(this, "没有查到信息！", Toast.LENGTH_LONG).show();
                }
                String[] names = cursor.getColumnNames();
                while (cursor.moveToNext()) {
                    String stu_no = cursor.getString(cursor.getColumnIndex(names[0]));
                    //插入到dianmingtb中
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("stu_no", stu_no);
                    contentValues.put("week", aweek);
                    contentValues.put("class", aclassno);
                    contentValues.put("status", "全勤");
                    mSQLiteDatabase.insert("dianmingtb", null, contentValues);
                }
            }


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
        } else {
            Toast.makeText(this, "未选择班级！", Toast.LENGTH_LONG).show();
        }
        mMyAdapterstu.notifyDataSetChanged();
    }

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
        abitmap = null;
        new Thread(new Runnable() {
            @Override
            public void run() {
        /*此处为获取照片的代码*/
                if (touxiangpath != "") {
                    File file = new File(touxiangpath);
                    File[] files = file.listFiles();
                    if (files.length > 0) {
                        for (int i = 0; i < files.length; i++) {
                            String filename = files[i].getName();
                            if (filename.contains(".")) {
                                filename = filename.substring(0, filename.lastIndexOf("."));
                            }
                            if (filename.equals(studentdianming.getStu_no())) {
                                try {
                                    abitmap = BitmapFactory.decodeStream(new FileInputStream(files[i]));
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            imageView.setImageBitmap(abitmap);
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(MainActivity.this, "头像导入错误！", Toast.LENGTH_LONG).show();
                                }
                                break;
                            }
                        }
                    }
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
        if (dianmingmethod.equals("全点")) {
            builder.setPositiveButton("下一个", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (ischanged) {
                        ischanged = false;
                        String sta = studentdianming.getStatus();
                        double badcount = studentdianming.getBadcount();
                        if ((oldstatus.equals("全勤")||oldstatus.equals("请假")) && (!sta.equals("全勤")&&!sta.equals("请假"))) {
                            badcount = badcount + 1;
                        } else if ((!oldstatus.equals("全勤")&&!oldstatus.equals("请假")) && (sta.equals("全勤")||sta.equals("请假"))) {
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
        } else {
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (ischanged) {
                        ischanged = false;
                        //更新数据库
                        String sta = studentdianming.getStatus();
                        double badcount = studentdianming.getBadcount();
                        if ((oldstatus.equals("全勤")||oldstatus.equals("请假")) && (!sta.equals("全勤")&&!sta.equals("请假"))) {
                            badcount = badcount + 1;
                        } else if ((!oldstatus.equals("全勤")&&!oldstatus.equals("请假")) && (sta.equals("全勤")||sta.equals("请假"))) {
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
        }
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
