package org.games.ukodus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class PuzzleView extends View {
	private static final String TAG = "Ukodus";
	private final Game game;
	private int N;

	private String[] buttonStrings = {"Prev" ,"" ,"Next"};
	private int z;
	private int viewW;
	private int viewH;
	private int boxH;
	private int Bwidth;
	private float width;
	private float height;
	private int selX;
	private int selY;
	private final Rect selRect = new Rect();
	private final Rect box = new Rect();

	private int secs;
	private Handler handler = new Handler();
	private Runnable runnable = new Runnable(){
		public void run(){
			secs++;
			
			if (secs % 10 == 9){
				z++;
				if (z == N)
					z = 0;
				invalidate();
			}
			buttonStrings[1] = "" + secs;
			invalidate(0,(int) (boxH + height),viewW, viewH);
			handler.postDelayed(this, 1000);
		}
	};
	
	/**
	 * Constructor
	 * creates a new instance of the PuzzleView class.
	 * context is an instance of Game.
	 * @param context
	 */
	public PuzzleView (Context context){
			super(context);
			this.game = (Game) context;
			this.N = this.game.getN();
			this.secs = 0;
			this.handler.postDelayed(runnable, 1);
			setFocusable(true);
			setFocusableInTouchMode(true);
	}

	protected void onDraw(Canvas canvas){
		//symbols to be used
		//draw the background
		Paint background = new Paint();
		background.setColor(getResources().getColor(R.color.puzzle_background));
		canvas.drawRect(0, 0, viewW, viewH, background);
	
		//Draw the board...
		
		//define grid box colors
		Paint grid = new Paint();
		grid.setColor(getResources().getColor(R.color.grid_background));
	
		//draw grid box
		canvas.drawRect(0, 0, viewW, boxH, grid);

		//define grid line colors
		Paint dark = new Paint();
		dark.setColor(R.color.puzzle_dark);
		Paint hilite = new Paint();
		hilite.setColor(R.color.puzzle_hilite);
		
		//draw the grid lines
		for (int i = 0; i < N; i++){
			canvas.drawLine(0, i * boxH / N, viewW, i * boxH / N, dark);
			canvas.drawLine(0, i * boxH / N + 1, viewW, i * boxH / N + 1, hilite);
			canvas.drawLine(i * viewW / N, 0, i * viewW / N, boxH, dark);
			canvas.drawLine(i * viewW / N + 1, 0, i * viewW / N, boxH + 1, hilite);		
		}
		
		//Draw the symbols
		//Define colors and style
		Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
		foreground.setColor(getResources().getColor(R.color.puzzle_foreground));
		foreground.setStyle(Style.FILL);
		foreground.setTextSize(height * 0.75f);
		foreground.setTextScaleX(width / height);
		foreground.setTextAlign(Paint.Align.CENTER);
		
		//draw the symbols in the center of the tile
		FontMetrics fm = foreground.getFontMetrics();
		float x = width /2;
		float y = height / 2 - (fm.ascent + fm.descent) / 2;
		
		for (int i = 0; i < N; i++){
			for(int j = 0; j < N; j++){
				canvas.drawText(game.getTileString(z,i,j),
						j * width + x,
						i * height + y,
						foreground);
			}
		}
		
		//draw keypad
		//define keypad box colors
		Paint keypad_back = new Paint();
		keypad_back.setColor(getResources().getColor(R.color.keypad_background));
		//draw keypad box
		canvas.drawRect(0, boxH, viewW, boxH + height, keypad_back);
		
		//draw dividing lines for keypad
		Paint keypad_light = new Paint();
		keypad_light.setColor(getResources().getColor(R.color.keypad_light));
		Paint keypad_hilite = new Paint();
		keypad_hilite.setColor(getResources().getColor(R.color.keypad_hilite));
		for (int i = 0; i < N; i++){
			canvas.drawLine(i * viewW / N, boxH, i * viewW /N, boxH + height, keypad_light);
			canvas.drawLine(i * viewW / N + 1, boxH, i * viewW / N + 1, boxH + height, keypad_hilite);
		}
		for (int i = 0; i < 3; i++){
			canvas.drawLine(i * viewW / 3, boxH + height, i * viewW / 3, getHeight(), keypad_light);
			canvas.drawLine(i * viewW / 3 + 1, boxH + height, i * viewW / 3 + 1, getHeight(), keypad_light);
		}
		
		//Draw the symbols
		//Define colors and style
		Paint keypadText = new Paint(Paint.ANTI_ALIAS_FLAG);
		keypadText.setColor(getResources().getColor(R.color.keypad_text));
		keypadText.setStyle(Style.FILL);
		keypadText.setTextSize(height * 0.75f);
		keypadText.setTextScaleX(width / height);
		keypadText.setTextAlign(Paint.Align.CENTER);
		
		
		//draw the symbols in the center of the tile

		x = width /2;
		y = height / 2 - (fm.ascent + fm.descent) / 2;
		for (int i = 0; i < N; i++){
				canvas.drawText(game.getSymbol(i+1),
						i * width + x,
						boxH + y,
						keypadText);
			}	
		canvas.drawLine(0, boxH + height, viewW, boxH + height, keypadText);
		
		//draw Buttons
		Paint buttonText = new Paint(Paint.ANTI_ALIAS_FLAG);
		buttonText.setColor(getResources().getColor(R.color.keypad_text));
		buttonText.setStyle(Style.FILL);
		buttonText.setTextSize(height * 0.3f);
		buttonText.setTextScaleX(Bwidth / height);
		buttonText.setTextAlign(Paint.Align.CENTER);
		fm = buttonText.getFontMetrics();
		
		x = Bwidth /2;
		y = height /2 -(fm.ascent + fm.descent) / 2;
		for (int i = 0; i < 3; i++){
			canvas.drawText(buttonStrings[i], i * viewW /3 + x, boxH + height + y, buttonText);
		}
		
		//Draw the selection ...
		Paint selected = new Paint();
		selected.setColor(getResources().getColor(R.color.puzzle_selected));
		canvas.drawRect(selRect, selected);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh){
		viewW = w;
		viewH = h;
		width = w / N;
		Bwidth = w / 3;
		height = h / (N + 2);
		boxH = (int) (N * height);
		box.set(0, 0, viewW, boxH);
		getRect(selX,selY, selRect);
		Log.d(TAG, "onSizeChanged width: " + w + " height: " + h);
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	protected void getRect(int x, int y, Rect rect){
		rect.set((int)(x * width),
				(int) (y * height),
				(int) (x * width + width),
				(int) (y * height + height));
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		if(event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);
		float touchX = event.getX();
		float touchY = event.getY();
		//if touch was on the grid
		if (touchY < boxH){
			select((int) (touchX / width)
				 , (int)(touchY / height));
			Log.d(TAG, "onTouchEVENT: x " + selX + ", y" + selY);
			return true;
		} else if (touchY < boxH + height){
			setSelectedTile((int)((touchX / width)+1));
			Log.d(TAG, "onTouchEVENT: keypad " + (int)((touchX /width)+1));
			return true;
		} else {
			Log.d(TAG, "onTouchEvent: Bottom");
			//prev
			//secs = 11;
			if( touchX < viewW/3){
				if ( z == 0)
					z = N;
				z--;
				Log.d(TAG, "onTouchEvent: Prev z=" + z);
				invalidate();
			}else if (touchX > 2 * viewW / 3){
			//next
				z++;
				if (z == N)
					z = 0;
				Log.d(TAG, "onTouchEvent: NEXT z=" + z);
				invalidate();
			}
			return true;
		}
	}
	
	private void select(int x, int y){
		invalidate(selRect);
		selX = Math.min(Math.max(x, 0), N -1);
		selY = Math.min(Math.max(y, 0), N- 1);
		getRect(selX, selY, selRect);
		invalidate(selRect);
	}

	public void setSelectedTile(int tile){
		switch (game.setTileIfValid(z, selY, selX, tile)){
			case -1:
				Log.d(TAG, "setSelectedTile: invalid: " + tile);
				break;
			case 0: 
				Log.d (TAG, "setSelectedTile: valid: " + tile + "x=" + selX + " y=" + selY);
				invalidate();
				break;
			case 1:
				Log.d(TAG, "setSelectedTile: WIN!?!?!)");
				//Display Win Screen
				break;
		}
	}
}
	/*
	@Override
	public boolean onKeyDown(int keyCode, keyEvent event){
		Log.d(TAG, "onKeyDown: keycode=" + keyCode + ", event=" + event);
		switch(keyCode){
		case KeyEvent.KEYCODE_DPAD_UP:
			select(selX, selY - 1);
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			select(selX, selY + 1);
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			select(selX - 1, selY);
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			select(selX + 1, selY);
			break;
		case KeyEvent.KEYCODE_0:
		case KeyEvent.KEYCODE_SPACE: setSelectedTile(0); break;
		case KeyEvent.KEYCODE_1: setSelectedTile(1); break;
		case KeyEvent.KEYCODE_2: setSelectedTile(2); break;
		case KeyEvent.KEYCODE_3: setSelectedTile(3); break;
		case KeyEvent.KEYCODE_4: setSelectedTile(4); break;
		case KeyEvent.KEYCODE_5: setSelectedTile(5); break;
		case KeyEvent.KEYCODE_6: setSelectedTile(6); break;
		case KeyEvent.KEYCODE_7: setSelectedTile(7); break;
		case KeyEvent.KEYCODE_8: setSelectedTile(8); break;
		case KeyEvent.KEYCODE_9: setSelectedTile(9); break;
		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_DPAD_CENTER:
			game.showKeypadOrError(selX,selY);
			break;
		default:
			return super.onKeyDown(keyCode, event);
		}
		
		return true;
	}
	*/

