// Strong Hold BOB 2016 Commands (Shooter Go)

package org.usfirst.frc319.commands.shooter;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc319.Robot;

/**
 *
 */ 



public class ShooterSpeedUp extends Command {

	private static int atSpeedCounter =0;
	private static int numberOfSamplesAtSpeed= 30;
	
    public ShooterSpeedUp() {
        requires(Robot.shooter);

    }

    protected void initialize() {
    	atSpeedCounter = 0;
    	
    }

    protected void execute() {
    	
    	if(Robot.shooter.isShooterAtSpeed()){
    		atSpeedCounter++;
    		System.out.println("atSpeedCOunter"+atSpeedCounter);
    	}
    	//possibly add an elseif its not at speed reset the counter
    	
    	double speed = 4000;
    	Robot.shooter.setLeftShooterSpeed(speed);
    	Robot.shooter.setRightShooterSpeed(speed);
    	
    }

    protected boolean isFinished() {
        return atSpeedCounter>numberOfSamplesAtSpeed;
    }

    protected void end() {
    	atSpeedCounter = 0;
    }

    protected void interrupted() {
    }
    
}
