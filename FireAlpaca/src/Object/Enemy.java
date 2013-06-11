package Object;

import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import scene.GameScene;
import scene.GameScene.Map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.example.manager.ResourcesManager;
import com.example.manager.SceneManager;

import extra.CoolDown;
public abstract class Enemy extends AnimatedSprite{
	private Body body;
	private float health;
	private int x, y;
	private Player player;
	public Enemy(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld, 
			ITiledTextureRegion region, Player player)
	{	
		super(pX, pY, region, vbo);	
		String userData;
		if (region == ResourcesManager.getInstance().blue_enemy_region)
		{
			userData = "blueEnemy";
			health = 1;
		}
		else if (region == ResourcesManager.getInstance().red_enemy_region)
		{
			userData = "redEnemy";
			health = 5;
		}
		else if(region == ResourcesManager.getInstance().yellow_enemy_region)
		{
			userData = "yellowEnemy";
			health = 3;
		} else { 
			userData = "boss"; 
			health = 100;
		}
		createPhysics(camera, physicsWorld, userData);
		this.player = player;
		final long[] ENEMY_ANIMATE = new long[] { 500, 500, 500 };
	    animate(ENEMY_ANIMATE, 0, 2, true);
	    this.x = (int) (pX-10)/20;
	    this.y = (int) (pY-10)/20;
		
	}
	
	public abstract void onDie();
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld, String userData)
	{
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		body.setUserData(userData);
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false)
		{
			@Override
			public void onUpdate(float pSecondsElapsed) 
			{
				super.onUpdate(pSecondsElapsed);
				enemy_move(body);
				if ((int)(getX()-10)/20 !=x || (int)(getY()-10)/20 != y) {
					Scene scene =  SceneManager.getInstance().getCurrentScene();
					if (scene instanceof GameScene) {
					((GameScene)scene).map[x][y] = Map.EMPTY;
					x = (int)(getX()-10)/20;
					y = (int)(getY()-10)/20;
					((GameScene)scene).map[x][y] = Map.ENEMY;
					}
				}
				
			}	
		});
		
	}
	
	//determine if the player is in range
	private boolean checkRange () 
	{
		return (Math.abs(player.getX()-this.getX())<=80 ||Math.abs(player.getY()-this.getX())<=80);
	
	}
	
	//determine if the enemy is close enough to start shooting
	private boolean checkShoot()
	{	
		GameScene scene = (GameScene) SceneManager.getInstance().getCurrentScene();
		boolean result = true;
		int player_x = (int)(player.getX()-10)/20;
		int player_y = (int)(player.getY()-10)/20;
		int enemy_x = (int)(this.getX()-10)/20;
		int enemy_y = (int) (this.getY()-10)/20;
		int x_distance = Math.abs(player_x - enemy_x);
		int y_distance = Math.abs(player_y - enemy_y);
		
		
		if (y_distance != 0 && x_distance != 0) {
			result = false;
		}
		/*else if (y_distance == 0) {
			if (player_x > enemy_x) {
				for (int x = enemy_x+1; x <player_x; x++){
					if (scene.map[x][enemy_y] == Map.STONE) {
						result = false;
					}
				}	
			}
			else {
				for (int x = player_x+1; x <enemy_x; x++){
					if (scene.map[x][enemy_y] == Map.STONE) {
						result = false;
					}
				}	
			}
		
		}
		
		else if (x_distance ==0) {
			if (player_y > enemy_y) {
				for (int y = enemy_y+1; y <player_y; y++){
					if (scene.map[enemy_x][y] == Map.STONE) {
						result = false;
					}
				}
			}
			else {
				for (int y = player_y+1; y <enemy_y; y++){
					if (scene.map[enemy_x][y] == Map.STONE) {
						result = false;
					}
				}	
			}
		}*/
		return result;
	}
	
	public abstract void enemy_shoot() ;
	
	//enemy action update 
	private void enemy_move(Body body)
	{	

			if (checkShoot()) {
				//enemy_shoot();
			}
	
		
		/*if (enemy_collide()){
				//deal with collision
		}*/
		enemy_random_move(body);
		
	}
	


	private void enemy_random_move(Body body) {
		int XVelocity; 
        int YVelocity;  
        Random randomGenerator = new Random();
        
        XVelocity = (int)randomGenerator.nextGaussian();
        YVelocity = (int)randomGenerator.nextGaussian();
        body.setLinearVelocity(new Vector2(XVelocity*5, YVelocity*5)); 
        

	}



	private boolean enemy_collide() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public Body get_body() {
		return body;
	}

	public float get_health() {
		return health;
	}
	
	public void set_health(float health) {
		this.health = health;
	}

	
	
	
	
	
	
	
}