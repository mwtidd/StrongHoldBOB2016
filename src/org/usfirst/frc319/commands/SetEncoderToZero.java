// Strong Hold BOB 2016 Commands (Collector In)

package org.usfirst.frc319.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc319.Robot;

/**
 *
 */
public class SetEncoderToZero extends Command {

    public SetEncoderToZero() {

        requires(Robot.collector);

    }

    protected void initialize() {
    }

    protected void execute() {
    	Robot.collector.setCollectorEncoderToZero();
    }

    protected boolean isFinished() {
        return false;
    }

    protected void end() {
    }

    protected void interrupted() {
    }
    
}
