package com.echo.dzmc;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbAdapter {

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	private static String DATABASE_NAME;
	private static String DATABASE_PATH;
	
	private static final int DATABASE_VERSION = 1;
	
	private static String TABLE_UNIT = "tbl_unit";
	private static String TABLE_GBMC = "tbl_gbmc";
	private static String TABLE_RELATE = "tbl_relate";
	
	private static String COL_UNIT_ID = "id";
	private static String COL_UNIT_NAME = "name";
	private static String COL_UNIT_PID = "pid";
	
	private static String COL_GBMC_ID = "id";
	private static String COL_GBMC_XM = "姓名";
	private static String COL_GBMC_DWDM = "单位代码";
	private static String COL_GBMC_ZW = "职务";
	private static String COL_GBMC_XB = "性别";
	private static String COL_GBMC_CSNY = "出生年月";
	private static String COL_GBMC_RDSJ = "入党时间";
	private static String COL_GBMC_CJGZSJ = "参加工作时间";
	private static String COL_GBMC_WHCD = "文化程度";
	private static String COL_GBMC_BYYX = "毕业院校";
	private static String COL_GBMC_JG = "籍贯";
	private static String COL_GBMC_MZ = "民族";
	private static String COL_GBMC_CSD = "出生地";
	private static String COL_GBMC_ZJ = "职级";
	private static String COL_GBMC_JKZK = "健康状况";
	private static String COL_GBMC_ZZJY = "在职教育";
	private static String COL_GBMC_ZZBYYX = "在职毕业院校";
	private static String COL_GBMC_JL = "简历";
	private static String COL_GBMC_XP = "相片";
	private static String COL_GBMC_JCQK = "奖惩情况";
	private static String COL_GBMC_NDKH = "年度考核";
	private static String COL_GBMC_DXPXQK = "党校培训";
	private static String COL_GBMC_XH = "序号";
	
	private static String COL_RELATE_ID = "id";
	private static String COL_RELATE_CW = "称谓";
	private static String COL_RELATE_XM = "姓名";
	private static String COL_RELATE_NL = "年龄";
	private static String COL_RELATE_ZZMM = "政治面貌";
	private static String COL_RELATE_DWZW = "单位职务";
	
	
	private final Context mCtx;
	
	private static String MSG_ERROR;
	private static String MSG_NODATABASE;
	private static String DB_DROPDB;
	
 	public static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context){
			super(context,DATABASE_PATH+DATABASE_NAME,null,DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			//db.execSQL(DATABASE_CREAT);
			//找不到数据库的提示
			Log.e(MSG_ERROR,MSG_NODATABASE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL(DB_DROPDB);
			onCreate(db);
		}
		
	}
	
	public DbAdapter(Context ctx,String dbPath,String dbName){
		mCtx = ctx;
		DATABASE_PATH = dbPath;
		DATABASE_NAME = dbName;
		MSG_ERROR = ctx.getString(R.string.MSG_ERROR);
		MSG_NODATABASE = ctx.getString(R.string.MSG_NODATABASE);
		DB_DROPDB = ctx.getString(R.string.DB_DROPDB);
	}
	
	public DbAdapter open() throws SQLException{
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		mDbHelper.close();
	}
	
	public void closeclose(){
		mDbHelper.close();
	}
	
	/**
	 * 取得单位byPID
	 * @param pid  单位父ID
	 * @return 单位Cursor
	 */
	public Cursor getUnitByPid(long pid){
		String[] columns = new String[] {COL_UNIT_NAME,COL_UNIT_ID+" as _id"};
		String selection = COL_UNIT_PID+"=?";
		String[] selectionArgs = new String[] {String.valueOf(pid)};
		String groupBy = null;
		String having = null;
		String orderBy = COL_UNIT_ID;
		return	mDb.query(TABLE_UNIT, columns, selection, selectionArgs, groupBy, having, orderBy);
	}
	
	/**
	 * 返回指定单位人员	
	 * @param uid 单位ID
	 * @return 指定单位人员Cursor
	 */
	public Cursor getUsersByUnitID(long uid){
		String[] columns = new String[] {COL_GBMC_ID+" as _id ",COL_GBMC_XM,COL_GBMC_ZW,COL_GBMC_ZJ,COL_GBMC_XP};
		String selection = "(" + COL_GBMC_DWDM + "=? or " + COL_GBMC_DWDM + " in (select " + COL_UNIT_ID + " from " + TABLE_UNIT + " where " + COL_UNIT_PID + "=?)) ";
		String[] selectionArgs = new String[] {String.valueOf(uid),String.valueOf(uid)};
		String groupBy = null;
		String having = null;
		String orderBy = COL_GBMC_XH;
		return	mDb.query(TABLE_GBMC, columns, selection, selectionArgs, groupBy, having, orderBy);
	}
	
	
	/**
	 * 取得干部信息
	 * @param id 人员ID
	 * @return 人员信息Cursor
	 */
	public Cursor getUserByID(long id){
		String[] columns = new String[] {COL_GBMC_ID+" as _id ",COL_GBMC_XM,COL_GBMC_ZJ,COL_GBMC_ZW,COL_GBMC_XP};
		String selection =  COL_GBMC_ID + "=?";
		String[] selectionArgs = new String[] {String.valueOf(id)};
		String groupBy = null;
		String having = null;
		String orderBy = null;
		return mDb.query(TABLE_GBMC, columns, selection, selectionArgs, groupBy, having, orderBy);
	}
	
	/**
	 * 取得干部信息
	 * @param id 人员ID
	 * @return 人员信息Cursor
	 */
	public Cursor getUserInfoByID(long id){
		String[] columns = new String[] {COL_GBMC_ID+" as _id ",COL_GBMC_XM,COL_GBMC_XP,COL_GBMC_XB,COL_GBMC_CSNY,COL_GBMC_MZ,COL_GBMC_JG,COL_GBMC_CSD,COL_GBMC_RDSJ,COL_GBMC_CJGZSJ,COL_GBMC_JKZK,COL_GBMC_WHCD,COL_GBMC_BYYX,COL_GBMC_ZZJY,COL_GBMC_ZZBYYX,COL_GBMC_ZW,COL_GBMC_JL,COL_GBMC_JCQK,COL_GBMC_NDKH,COL_GBMC_DXPXQK};
		String selection =  COL_GBMC_ID + "=?";
		String[] selectionArgs = new String[] {String.valueOf(id)};
		String groupBy = null;
		String having = null;
		String orderBy = null;
		return mDb.query(TABLE_GBMC, columns, selection, selectionArgs, groupBy, having, orderBy);
	}	
	
	/**
	 * 取得亲属信息
	 * @param id 干部ID
	 * @return 亲属 Cursor
	 */
	public Cursor getRelateById(long id){
		String[] columns = new String[] {COL_RELATE_ID+" as _id ",COL_RELATE_CW,COL_RELATE_XM,COL_RELATE_NL,COL_RELATE_ZZMM,COL_RELATE_DWZW};
		String selection =  COL_RELATE_ID + "=?";
		String[] selectionArgs = new String[] {String.valueOf(id)};
		String groupBy = null;
		String having = null;
		String orderBy = null;
		return mDb.query(TABLE_RELATE, columns, selection, selectionArgs, groupBy, having, orderBy);
	}
}
