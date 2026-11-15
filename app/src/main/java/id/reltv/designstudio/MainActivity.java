package id.reltv.designstudio;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.*;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import org.json.*;
import java.io.*;
import java.util.*;

public class MainActivity extends Activity {
    final int NAVY=Color.rgb(16,36,58), BLUE=Color.rgb(29,78,121), GOLD=Color.rgb(224,177,82);
    LinearLayout root, body;
    ArrayList<Tpl> templates=new ArrayList<>();
    Tpl active;
    DesignView preview;
    EditText headingInput, subInput;
    Bitmap photo;
    int bg=Color.WHITE, fg=NAVY;
    String[] cats={"Ucapan","Infografis","Poster","Undangan","Quotes","Lainnya"};
    int[] counts={25,25,20,15,15,20};
    static class Tpl {
        String name,cat,head,sub; int bg,fg;
        Tpl(String n,String c,String h,String s,int b,int f){name=n;cat=c;head=h;sub=s;bg=b;fg=f;}
    }
    @Override public void onCreate(Bundle b){super.onCreate(b); getWindow().setStatusBarColor(NAVY);getWindow().setNavigationBarColor(NAVY); buildTemplates(); showHome();}
    int dp(float v){return (int)(v*getResources().getDisplayMetrics().density);}
    TextView text(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);return t;}
    Button button(String s){Button b=new Button(this);b.setText(s);b.setAllCaps(false);return b;}
    void base(String title){root=new LinearLayout(this);root.setOrientation(1);root.setBackgroundColor(0xfff3f6fa);setContentView(root);
        TextView bar=text(title,20,Color.WHITE);bar.setTypeface(null,1);bar.setGravity(Gravity.CENTER_VERTICAL);bar.setPadding(dp(18),dp(14),dp(12),dp(14));bar.setBackgroundColor(NAVY);root.addView(bar,new LinearLayout.LayoutParams(-1,dp(58)));
        ScrollView sc=new ScrollView(this);body=new LinearLayout(this);body.setPadding(dp(14),dp(14),dp(14),dp(24));body.setOrientation(1);sc.addView(body);root.addView(sc,new LinearLayout.LayoutParams(-1,0,1));}
    void buildTemplates(){String[] heads={"Selamat Ulang Tahun","Selamat & Sukses","Terima Kasih","Selamat Hari Raya","Undangan Istimewa","Inspirasi Hari Ini","Info Penting","Jadwal Kegiatan","Pengumuman","Kenangan Indah"};
        String[] subs={"Semoga bahagia dan sehat selalu","Semoga langkahmu dipenuhi keberhasilan","Terima kasih atas perhatian dan dukungan","Mohon maaf lahir dan batin","Dengan hormat kami mengundang Anda","Terus melangkah, jangan menyerah","Informasi untuk kita semua","Catat waktu dan agendanya","Mari simak informasi berikut","Momen sederhana yang bermakna"};
        int[][] palette={{0xfff8f3e8,NAVY},{0xffe8f1f8,BLUE},{0xfff8e9ed,0xff842c48},{0xffe9f2e9,0xff28583c},{0xfff1eafb,0xff563a82},{0xfffff0d9,0xff80551b}};
        int k=0;for(int c=0;c<cats.length;c++)for(int i=0;i<counts[c];i++){int[] p=palette[(k)%palette.length];templates.add(new Tpl(cats[c]+" "+String.format(Locale.US,"%02d",i+1),cats[c],heads[k%heads.length],subs[k%subs.length],p[0],p[1]));k++;}}
    void showHome(){base("ReL DESIGN STUDIO");
        TextView intro=text("Berkreasi dengan mudah, bahkan tanpa internet.",16,NAVY);intro.setPadding(0,0,0,dp(14));body.addView(intro);
        Button create=button("＋  Buat Desain Baru");create.setTextColor(Color.WHITE);create.setBackgroundTintList(android.content.res.ColorStateList.valueOf(BLUE));body.addView(create);create.setOnClickListener(v->showCategories());
        TextView label=text("PILIH KATEGORI TEMPLATE",13,0xff667788);label.setPadding(0,dp(20),0,dp(8));body.addView(label);
        for(int i=0;i<cats.length;i++){final String cat=cats[i];Button b=button(cat+"  •  "+counts[i]+" template");body.addView(b);b.setOnClickListener(v->showCatalog(cat));}
        Button saved=button("Desain Saya  •  Buka desain tersimpan");body.addView(saved);saved.setOnClickListener(v->showSaved());
        TextView foot=text("120 template awal tersedia offline. Koleksi tambahan gratis akan disiapkan pada tahap berikutnya.",13,0xff667788);foot.setPadding(0,dp(18),0,0);body.addView(foot);}
    void showCategories(){base("PILIH KATEGORI");for(String c:cats){Button b=button(c);body.addView(b);b.setOnClickListener(v->showCatalog(c));}}
    void showCatalog(String cat){base(cat.toUpperCase()+" • TEMPLATE");for(Tpl t:templates)if(t.cat.equals(cat)){LinearLayout card=new LinearLayout(this);card.setOrientation(1);card.setPadding(dp(12),dp(8),dp(12),dp(8));card.setBackgroundColor(Color.WHITE);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(74));lp.bottomMargin=dp(8);body.addView(card,lp);
        TextView name=text(t.name,15,NAVY);name.setTypeface(null,1);card.addView(name);TextView sample=text(t.head+" — "+t.sub,12,0xff667788);card.addView(sample);card.setOnClickListener(v->openEditor(t));}}
    void openEditor(Tpl t){active=t;bg=t.bg;fg=t.fg;photo=null;showEditor();}
    void showEditor(){base("EDITOR DESAIN");TextView tip=text("Pilih teks, warna, atau foto. Geser elemen pada pratinjau untuk mengatur posisinya.",13,0xff566575);body.addView(tip);
        preview=new DesignView();LinearLayout.LayoutParams pp=new LinearLayout.LayoutParams(-1,dp(390));pp.topMargin=dp(12);pp.bottomMargin=dp(12);body.addView(preview,pp);
        headingInput=new EditText(this);headingInput.setSingleLine(false);headingInput.setText(active.head);headingInput.setHint("Judul desain");body.addView(headingInput);
        subInput=new EditText(this);subInput.setText(active.sub);subInput.setHint("Teks keterangan");body.addView(subInput);
        Button apply=button("Terapkan perubahan teks");body.addView(apply);apply.setOnClickListener(v->{active.head=headingInput.getText().toString();active.sub=subInput.getText().toString();preview.invalidate();});
        TextView colorLabel=text("WARNA LATAR",12,NAVY);colorLabel.setPadding(0,dp(12),0,0);body.addView(colorLabel);
        LinearLayout colors=new LinearLayout(this);body.addView(colors);int[] palette={Color.WHITE,0xfff8f3e8,0xffe8f1f8,0xfff8e9ed,0xffe9f2e9,0xff172b4d,0xff252525};for(int c:palette){Button b=button("●");b.setTextColor(c);colors.addView(b,new LinearLayout.LayoutParams(0,dp(48),1));b.setOnClickListener(v->{bg=c;preview.invalidate();});}
        Button addPhoto=button("Pilih Foto dari Galeri");body.addView(addPhoto);addPhoto.setOnClickListener(v->{Intent i=new Intent(Intent.ACTION_GET_CONTENT);i.setType("image/*");startActivityForResult(i,77);});
        Button save=button("Simpan Desain");body.addView(save);save.setOnClickListener(v->saveDesign());
        Button png=button("Ekspor PNG");body.addView(png);png.setOnClickListener(v->exportImage());
        Button pdf=button("Ekspor PDF");body.addView(pdf);pdf.setOnClickListener(v->exportPdf());
        Button home=button("Kembali ke Katalog");body.addView(home);home.setOnClickListener(v->showCatalog(active.cat));}
    class DesignView extends View{
        Paint p=new Paint(3);float hx,hy;boolean move=false;DesignView(){super(MainActivity.this);setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
        void drawDesign(Canvas c,int w,int h){p.setColor(bg);p.setStyle(Paint.Style.FILL);c.drawRect(0,0,w,h,p);
            p.setColor(fg);p.setStrokeWidth(Math.max(4,w*.012f));p.setStyle(Paint.Style.STROKE);c.drawRoundRect(w*.06f,h*.04f,w*.94f,h*.96f,w*.04f,w*.04f,p);p.setStyle(Paint.Style.FILL);
            if(photo!=null){Rect dst=new Rect((int)(w*.18),(int)(h*.12),(int)(w*.82),(int)(h*.48));p.setColor(Color.WHITE);c.drawBitmap(photo,null,dst,p);}
            p.setColor(fg);p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));p.setTextSize(w*.085f);
            String head=active.head;drawWrapped(c,head,w/2f,photo==null?h*.40f:h*.62f,w*.78f,w*.10f,p);
            p.setTypeface(Typeface.create("sans-serif",Typeface.NORMAL));p.setTextSize(w*.042f);drawWrapped(c,active.sub,w/2f,photo==null?h*.57f:h*.75f,w*.76f,w*.065f,p);
            p.setTextSize(w*.027f);c.drawText("ReL DESIGN STUDIO",w/2f,h*.90f,p);}
        void drawWrapped(Canvas c,String s,float x,float y,float max,float line,Paint paint){if(s==null)return;String[] words=s.split(" ");String row="";float yy=y;for(String word:words){String test=row.isEmpty()?word:row+" "+word;if(paint.measureText(test)>max&&!row.isEmpty()){c.drawText(row,x,yy,paint);row=word;yy+=line;}else row=test;}if(!row.isEmpty())c.drawText(row,x,yy,paint);}
        @Override protected void onDraw(Canvas canvas){super.onDraw(canvas);drawDesign(canvas,getWidth(),getHeight());}
        @Override public boolean onTouchEvent(android.view.MotionEvent e){if(e.getAction()==MotionEvent.ACTION_DOWN){hx=e.getX();hy=e.getY();move=true;return true;}if(e.getAction()==MotionEvent.ACTION_UP){if(move&&Math.abs(e.getY()-hy)>dp(12)){String old=active.head;active.head=old;invalidate();}move=false;return true;}return true;}
        Bitmap bitmap(){Bitmap b=Bitmap.createBitmap(1080,1350,Bitmap.Config.ARGB_8888);Canvas c=new Canvas(b);drawDesign(c,1080,1350);return b;}
    }
    void saveDesign(){try{JSONObject o=new JSONObject();o.put("name",active.name);o.put("cat",active.cat);o.put("head",active.head);o.put("sub",active.sub);o.put("bg",bg);o.put("fg",fg);String raw=getPreferences(0).getString("saved","[]");JSONArray a=new JSONArray(raw);a.put(o);getPreferences(0).edit().putString("saved",a.toString()).apply();Toast.makeText(this,"Desain berhasil disimpan di perangkat",Toast.LENGTH_SHORT).show();}catch(Exception e){Toast.makeText(this,"Gagal menyimpan desain",Toast.LENGTH_SHORT).show();}}
    void showSaved(){base("DESAIN SAYA");try{JSONArray a=new JSONArray(getPreferences(0).getString("saved","[]"));if(a.length()==0){body.addView(text("Belum ada desain tersimpan.",15,NAVY));return;}for(int i=a.length()-1;i>=0;i--){JSONObject o=a.getJSONObject(i);Button b=button(o.optString("name")+" — "+o.optString("head"));body.addView(b);b.setOnClickListener(v->{active=new Tpl(o.optString("name"),o.optString("cat"),o.optString("head"),o.optString("sub"),o.optInt("bg"),o.optInt("fg"));bg=o.optInt("bg");fg=o.optInt("fg");showEditor();});}}catch(Exception e){}}
    File outputDir(){File d=new File(getExternalFilesDir(null),"ReLDesignStudio");if(!d.exists())d.mkdirs();return d;}
    void exportImage(){try{File f=new File(outputDir(),"Desain_"+System.currentTimeMillis()+".png");FileOutputStream out=new FileOutputStream(f);preview.bitmap().compress(Bitmap.CompressFormat.PNG,100,out);out.close();shareFile(f,"image/png");}catch(Exception e){Toast.makeText(this,"Ekspor PNG gagal: "+e.getMessage(),Toast.LENGTH_LONG).show();}}
    void exportPdf(){try{PdfDocument doc=new PdfDocument();PdfDocument.PageInfo info=new PdfDocument.PageInfo.Builder(595,842,1).create();PdfDocument.Page page=doc.startPage(info);Bitmap b=preview.bitmap();page.getCanvas().drawBitmap(b,null,new Rect(24,24,571,818),new Paint(3));doc.finishPage(page);File f=new File(outputDir(),"Desain_"+System.currentTimeMillis()+".pdf");FileOutputStream out=new FileOutputStream(f);doc.writeTo(out);out.close();doc.close();shareFile(f,"application/pdf");}catch(Exception e){Toast.makeText(this,"Ekspor PDF gagal: "+e.getMessage(),Toast.LENGTH_LONG).show();}}
    void shareFile(File f,String mime){Toast.makeText(this,"File tersimpan di folder Android/data/id.reltv.designstudio/files/ReLDesignStudio",Toast.LENGTH_LONG).show();Intent i=new Intent(Intent.ACTION_SEND);i.setType(mime);i.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(f));i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);try{startActivity(Intent.createChooser(i,"Bagikan desain"));}catch(Exception e){}}
    @Override protected void onActivityResult(int r,int c,Intent d){super.onActivityResult(r,c,d);if(r==77&&c==RESULT_OK&&d!=null){try{photo=android.provider.MediaStore.Images.Media.getBitmap(getContentResolver(),d.getData());if(preview!=null)preview.invalidate();}catch(Exception e){Toast.makeText(this,"Foto tidak dapat dibuka",Toast.LENGTH_SHORT).show();}}}
}