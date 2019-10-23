package org.firstinspires.ftc.teamcode;

//import com.disnodeteam.dogecv.CameraViewDisplay;
//import com.disnodeteam.dogecv.DogeCV;
//import com.disnodeteam.dogecv.detectors.roverrukus.GoldDetector;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotor.RunMode;
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
//import org.opencv.core.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;


public class Config {
    Servo Servo_A;
    DcMotor left_front, right_front, left_back, right_back;
    // A timer object
    private ElapsedTime timer = new ElapsedTime();
    private final int EncoderNumberChangePerInch = 34;
    // Get the important bits from the opMode
    private LinearOpMode OpMode;

    Config(LinearOpMode OpMode) {
        this.OpMode = OpMode;
    }



    //vision part
    private static final VuforiaLocalizer.CameraDirection CAMERA_CHOICE = BACK;
    private static final boolean PHONE_IS_PORTRAIT = false  ;
    private static final String VUFORIA_KEY =
            "AY2fMLf/////AAABmTcpSvf6l0P0nO4JSNywZ3gPjoh6efhgyl8JY4mDVIh6eVuG2gGzL5W6EYS7C9/o8GCbSMEChTxOmKEDWKQJLKtnTg5uVfGa99ZxHTk5bhGKOfr5j2/68p3/MCBlBUAz4doer2t9/vh5qZUs48mUKELA0LD8q52rMV5zUFsbEp2x9Rk1Wkt2FhQp1nSFbqqwEFeMOsSLmyfc9MYBA3nLxK2ITuN4Z7pdwmaO2yH7r6cOpA5qPMgLwPEaRC54ESiKHVFcKIUF5DpSMlGaoHMGvoND/PBNxUsapcVrKBKzLgTtagEXsZSODal109yv6DBZgI9vgEUoHHBB9mEJaZMROf/U55avUbg1FvIyBKSYzgzW";

    // We will define some constants and conversions here
    private static final float mmPerInch        = 25.4f;
    private static final float mmTargetHeight   = (6) * mmPerInch;          // the height of the center of the target image above the floor

    // Constant for Stone Target
    private static final float stoneZ = 2.00f * mmPerInch;

    // Constants for the center support targets
    private static final float bridgeZ = 6.42f * mmPerInch;
    private static final float bridgeY = 23 * mmPerInch;
    private static final float bridgeX = 5.18f * mmPerInch;
    private static final float bridgeRotY = 59;                                 // Units are degrees
    private static final float bridgeRotZ = 180;

    // Constants for perimeter targets
    private static final float halfField = 72 * mmPerInch;
    private static final float quadField  = 36 * mmPerInch;

    // Class Members
    private OpenGLMatrix lastLocation = null;
    private VuforiaLocalizer vuforia = null;
    boolean targetVisible = false;
    private float phoneXRotate    = 0;
    private float phoneYRotate    = 0;
    private float phoneZRotate    = 0;

    VuforiaTrackables targetsSkyStone = this.vuforia.loadTrackablesFromAsset("Skystone");

    VuforiaTrackable stoneTarget = targetsSkyStone.get(0);
    VuforiaTrackable blueRearBridge = targetsSkyStone.get(1);
    VuforiaTrackable redRearBridge = targetsSkyStone.get(2);
    VuforiaTrackable redFrontBridge = targetsSkyStone.get(3);
    VuforiaTrackable blueFrontBridge = targetsSkyStone.get(4);
    VuforiaTrackable red1 = targetsSkyStone.get(5);
    VuforiaTrackable red2 = targetsSkyStone.get(6);
    VuforiaTrackable front1 = targetsSkyStone.get(7);
    VuforiaTrackable front2 = targetsSkyStone.get(8);
    VuforiaTrackable blue1 = targetsSkyStone.get(9);
    VuforiaTrackable blue2 = targetsSkyStone.get(10);
    VuforiaTrackable rear1 = targetsSkyStone.get(11);
    VuforiaTrackable rear2 = targetsSkyStone.get(12);
    // For convenience, gather together all the trackable objects in one easily-iterable collection */
    List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();

    private boolean stoneFind=false;


    void ConfigureRobtHardware() {
        // Declare and setup left_front
        this.status("Configuring left front motor");
        this.left_front = OpMode.hardwareMap.dcMotor.get("left front");
        this.left_front.setZeroPowerBehavior(ZeroPowerBehavior.BRAKE);
        this.left_front.setMode(RunMode.STOP_AND_RESET_ENCODER);
        this.left_front.setMode(RunMode.RUN_USING_ENCODER);
        this.left_front.setDirection(Direction.FORWARD);

        // Declare and setup right_front
        this.status("Configuring right front motor");
        this.right_front = OpMode.hardwareMap.dcMotor.get("right front");
        this.right_front.setZeroPowerBehavior(ZeroPowerBehavior.BRAKE);
        this.right_front.setMode(RunMode.STOP_AND_RESET_ENCODER);
        this.right_front.setMode(RunMode.RUN_USING_ENCODER);
        this.right_front.setDirection(Direction.REVERSE);

        // Declare and setup left_back
        this.status("Configuring left back motor");
        this.left_back = OpMode.hardwareMap.dcMotor.get("left back");
        this.left_back.setZeroPowerBehavior(ZeroPowerBehavior.BRAKE);
        this.left_back.setMode(RunMode.STOP_AND_RESET_ENCODER);
        this.left_back.setMode(RunMode.RUN_USING_ENCODER);
        this.left_back.setDirection(Direction.FORWARD);

        // Declare and setup right_back
        this.status("Configuring right back motor");
        this.right_back = OpMode.hardwareMap.dcMotor.get("right back");
        this.right_back.setZeroPowerBehavior(ZeroPowerBehavior.BRAKE);
        this.right_back.setMode(RunMode.STOP_AND_RESET_ENCODER);
        this.right_back.setMode(RunMode.RUN_USING_ENCODER);
        this.right_back.setDirection(Direction.REVERSE);


        // Declare the servo
        this.status("Setting up left servo");
        this.Servo_A = OpMode.hardwareMap.servo.get("Servo A");
        this.status("Done!");
    }

    void ConfigureVision(){
        //the value of camera id is 2131099685(get from test)
        //this line of code below is not working because the hardwareMap only works in opmode
        //int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        int cameraMoniterViewId=2131099685;//this value is read from test with the zte phones on the practice drive train.
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMoniterViewId);

        // VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection   = CAMERA_CHOICE;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Load the data sets for the trackable objects. These particular data
        // sets are stored in the 'assets' part of our application.
        stoneTarget.setName("Stone Target");
        blueRearBridge.setName("Blue Rear Bridge");
        redRearBridge.setName("Red Rear Bridge");
        redFrontBridge.setName("Red Front Bridge");
        blueFrontBridge.setName("Blue Front Bridge");
        red1.setName("Red Perimeter 1");
        red2.setName("Red Perimeter 2");
        front1.setName("Front Perimeter 1");
        front2.setName("Front Perimeter 2");
        blue1.setName("Blue Perimeter 1");
        blue2.setName("Blue Perimeter 2");
        rear1.setName("Rear Perimeter 1");
        rear2.setName("Rear Perimeter 2");

        allTrackables.addAll(targetsSkyStone);

        // Set the position of the Stone Target.  Since it's not fixed in position, assume it's at the field origin.
        // Rotated it to to face forward, and raised it to sit on the ground correctly.
        // This can be used for generic target-centric approach algorithms
        stoneTarget.setLocation(OpenGLMatrix
                .translation(0, 0, stoneZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90)));

        //Set the position of the bridge support targets with relation to origin (center of field)
        blueFrontBridge.setLocation(OpenGLMatrix
                .translation(-bridgeX, bridgeY, bridgeZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0, bridgeRotY, bridgeRotZ)));

        blueRearBridge.setLocation(OpenGLMatrix
                .translation(-bridgeX, bridgeY, bridgeZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0, -bridgeRotY, bridgeRotZ)));

        redFrontBridge.setLocation(OpenGLMatrix
                .translation(-bridgeX, -bridgeY, bridgeZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0, -bridgeRotY, 0)));

        redRearBridge.setLocation(OpenGLMatrix
                .translation(bridgeX, -bridgeY, bridgeZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0, bridgeRotY, 0)));

        //Set the position of the perimeter targets with relation to origin (center of field)
        red1.setLocation(OpenGLMatrix
                .translation(quadField, -halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180)));

        red2.setLocation(OpenGLMatrix
                .translation(-quadField, -halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180)));

        front1.setLocation(OpenGLMatrix
                .translation(-halfField, -quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , 90)));

        front2.setLocation(OpenGLMatrix
                .translation(-halfField, quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 90)));

        blue1.setLocation(OpenGLMatrix
                .translation(-quadField, halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 0)));

        blue2.setLocation(OpenGLMatrix
                .translation(quadField, halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 0)));

        rear1.setLocation(OpenGLMatrix
                .translation(halfField, quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , -90)));

        rear2.setLocation(OpenGLMatrix
                .translation(halfField, -quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90)));

        //
        // Create a transformation matrix describing where the phone is on the robot.
        //
        // NOTE !!!!  It's very important that you turn OFF your phone's Auto-Screen-Rotation option.
        // Lock it into Portrait for these numbers to work.
        //
        // Info:  The coordinate frame for the robot looks the same as the field.
        // The robot's "forward" direction is facing out along X axis, with the LEFT side facing out along the Y axis.
        // Z is UP on the robot.  This equates to a bearing angle of Zero degrees.
        //
        // The phone starts out lying flat, with the screen facing Up and with the physical top of the phone
        // pointing to the LEFT side of the Robot.
        // The two examples below assume that the camera is facing forward out the front of the robot.

        // We need to rotate the camera around it's long axis to bring the correct camera forward.
        if (CAMERA_CHOICE == BACK) {
            phoneYRotate = -90;
        } else {
            phoneYRotate = 90;
        }

        // Rotate the phone vertical about the X axis if it's in portrait mode
        if (PHONE_IS_PORTRAIT) {
            phoneXRotate = 90 ;
        }

        // Next, translate the camera lens to where it is on the robot.
        // In this example, it is centered (left to right), but forward of the middle of the robot, and above ground level.
        final float CAMERA_FORWARD_DISPLACEMENT  = 8 * mmPerInch;   // eg: Camera is 0 Inches in front of robot center
        final float CAMERA_VERTICAL_DISPLACEMENT = 2.5f * mmPerInch;   // eg: Camera is 6 Inches above ground
        final float CAMERA_LEFT_DISPLACEMENT     = 0;     // eg: Camera is ON the robot's center line

        OpenGLMatrix robotFromCamera = OpenGLMatrix
                .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES, phoneYRotate, phoneZRotate, phoneXRotate));

        /**  Let all the trackable listeners know where the phone is.  */
        for (VuforiaTrackable trackable : allTrackables) {
            ((VuforiaTrackableDefaultListener) trackable.getListener()).setPhoneInformation(robotFromCamera, parameters.cameraDirection);
        }

    }


    void resetMotorsForAutonomous(DcMotor... motors) {
        for (DcMotor motor : motors) {
            motor.setPower(0);
            motor.setMode(RunMode.STOP_AND_RESET_ENCODER);
            motor.setTargetPosition(0);
            motor.setMode(RunMode.RUN_TO_POSITION);
        }
    }

    private boolean isThere(int error, DcMotor... motors) {
        boolean reached = false;
        for (DcMotor motor : motors) {
            int delta = Math.abs(motor.getTargetPosition() - motor.getCurrentPosition());
            if (delta <= error) {
                reached = true;
                break;
            }
        }
        return reached;
    }
    void distinctDrive(double speed, double LFInches, double LBInches, double RFInches, double RBInches, double timeoutS) {

        // Reset the motor encoders, and set them to RUN_TO_POSITION
        this.resetMotorsForAutonomous(this.left_front, this.left_back, this.right_front, this.right_back);

        // Set the individual drive motor positions
        this.left_front.setTargetPosition((int) Math.round(LFInches * EncoderNumberChangePerInch));
        this.right_front.setTargetPosition((int) Math.round(RFInches * EncoderNumberChangePerInch));
        this.left_back.setTargetPosition((int) Math.round(LBInches * EncoderNumberChangePerInch));
        this.right_back.setTargetPosition((int) Math.round(RBInches * EncoderNumberChangePerInch));

        // Set the motor speeds
        this.left_front.setPower(speed);
        this.right_front.setPower(speed);
        this.left_back.setPower(speed);
        this.right_back.setPower(speed);

        // Reset the runtime
        this.timer.reset();
        while (OpMode.opModeIsActive() && (this.timer.seconds() < timeoutS)) {

            // Check if the target has been reached
            if (this.isThere(4, this.left_back, this.left_front, this.right_back, this.right_front)) {
                // Break out of the while loop early
                break;
            }
        }
        // Stop all motion, and reset the motors
        this.resetMotorsForAutonomous(this.left_back, this.left_front, this.right_back, this.right_front);
    }

    double lookForStoneY(double timeoutS){
        this.timer.reset();
        stoneFind=false;
        while (OpMode.opModeIsActive() && (this.timer.seconds() < timeoutS)) {
            for (VuforiaTrackable trackable : allTrackables) {
                if (((VuforiaTrackableDefaultListener)trackable.getListener()).isVisible()) {
                    if (trackable.getName()=="Stone Target") {
                        OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)trackable.getListener()).getUpdatedRobotLocation();
                        if (robotLocationTransform != null) {
                            lastLocation = robotLocationTransform;
                        }
                        stoneFind=true;
                        break;
                    }
                }
            }
            if (stoneFind) {
                VectorF translation = lastLocation.getTranslation();
                return (translation.get(1)/mmPerInch);
            }
        }
        return (0);
    }


    //I am not sure what is the code below doing
    void status(String string) {
        this.OpMode.telemetry.addData("Status", string);
        this.OpMode.telemetry.update();
    }
}
