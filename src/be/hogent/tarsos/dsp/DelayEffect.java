/*
*
*  TarsosDSP is developed by Joren Six at 
*  The Royal Academy of Fine Arts & Royal Conservatory,
*  University College Ghent,
*  Hoogpoort 64, 9000 Ghent - Belgium
*  
*  http://tarsos.0110.be/tag/TarsosDSP
*
*/
/**
*
*  TarsosDSP is developed by Joren Six at 
*  The Royal Academy of Fine Arts & Royal Conservatory,
*  University College Ghent,
*  Hoogpoort 64, 9000 Ghent - Belgium
*  
*  http://tarsos.0110.be/tag/TarsosDSP
*
**/
package be.hogent.tarsos.dsp;


/**
 * <p>
 * Adds an echo effect to the signal.
 * </p>
 * 
 * @author Joren Six
 */
public class DelayEffect implements AudioProcessor {
	
	private double sampleRate;
	private float[] echoBuffer;//in seconds
	private int position;
	private float decay;
	
	private double newEchoLength;
	
	/**
	 * @param echoLength in seconds
	 * @param sampleRate the sample rate in Hz.
	 * @param decay The decay of the echo, a value between 0 and 1.
	 * @param overlap 
	 */
	public DelayEffect(double echoLength,double decay,double sampleRate) {
		this.sampleRate = sampleRate;
		setDecay(decay);
		setEchoLength(echoLength);
		applyNewEchoLength();	
	}
	
	/**
	 * @param newEchoLength A new echo buffer length in seconds.
	 */
	public void setEchoLength(double newEchoLength){
		this.newEchoLength = newEchoLength;
	}
	
	private void applyNewEchoLength(){
		if(newEchoLength != -1){
			
			//create a new buffer with the information of the previous buffer
			float[] newEchoBuffer = new float[(int) (sampleRate * newEchoLength)];
			if(echoBuffer != null){
				for(int i = 0 ; i < newEchoBuffer.length; i++){
					if(position >= echoBuffer.length){
						position = 0;
					}
					newEchoBuffer[i] = echoBuffer[position];
					position++;
				}
			}
			this.echoBuffer = newEchoBuffer;
			newEchoLength = -1;
		}
	}
	
	/**
	 * A decay, should be a value between zero and one.
	 * @param newDecay the new decay (preferably between zero and one).
	 */
	public void setDecay(double newDecay){
		this.decay = (float) newDecay;
	}
	
	@Override
	public boolean process(AudioEvent audioEvent) {
		float[] audioFloatBuffer = audioEvent.getFloatBuffer();
		int overlap = audioEvent.getOverlap();
			
		for(int i = overlap ; i < audioFloatBuffer.length ; i++){
			if(position >= echoBuffer.length){
				position = 0;
			}
			
			//output is the input added with the decayed echo 		
			audioFloatBuffer[i] = audioFloatBuffer[i] + echoBuffer[position] * decay;
			//store the sample in the buffer;
			echoBuffer[position] = audioFloatBuffer[i];
			
			position++;
		}
		
		applyNewEchoLength();
		
		return true;
	}

	@Override
	public void processingFinished() {		
	}
}
