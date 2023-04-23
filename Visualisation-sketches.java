//Spektralanalyse
if (fftDaten != null) {
            int numBars = fftDaten.length / 2 - 1;
            if (punkteZumZeichnen == null || punkteZumZeichnen.length < numBars * 4) {
                punkteZumZeichnen = new float[numBars * 4];
            }

            viewRect.set(0, 0, getWidth(), getHeight());

            float barWidth = (float) viewRect.width() / numBars;
            float maxMagnitude = 256f; // Der maximale Wert, den ein Byte annehmen kann

            for (int i = 0, j = 0; i < numBars; i++, j += 4) {
                // Logarithmische Skalierung der Balkenposition
                double scaledIndex = Math.pow(2, i * (Math.log(numBars) / Math.log(2) / numBars)) - 1;

                int fftIndex = (int) (scaledIndex * 2);
                float magnitude = (float) Math.hypot(fftDaten[fftIndex], fftDaten[fftIndex + 1]);
                float normalizedMagnitude = Math.min(1.0f, magnitude / maxMagnitude);
                float logScaledMagnitude = (float) (Math.log10(1 + normalizedMagnitude) * viewRect.height());

                float left = i * barWidth;
                float top = viewRect.height() - logScaledMagnitude;
                float right = left + barWidth;
                float bottom = viewRect.height();

                punkteZumZeichnen[j] = left;
                punkteZumZeichnen[j + 1] = top;
                punkteZumZeichnen[j + 2] = right;
                punkteZumZeichnen[j + 3] = bottom;
            }

            for (int i = 0; i < punkteZumZeichnen.length; i += 4) {
                canvas.drawRect(punkteZumZeichnen[i], punkteZumZeichnen[i + 1], punkteZumZeichnen[i + 2], punkteZumZeichnen[i + 3], paint);
            }
        }



//Zwei Halbkreise
if (fftDaten != null) {

                int sampleRate = 44100; // Angenommene Abtastrate
                int fftSize = fftDaten.length / 2; // Größe der FFT

                // Definieren Sie die Frequenzbereiche
                int lowerRangeStart = 1000; // 1 kHz
                int lowerRangeEnd = 7000; // 7 kHz
                int upperRangeStart = 7000; // 7 kHz
                int upperRangeEnd = 20000; // 20 kHz

                // Konvertiere die Frequenzbereiche in FFT-Bins
                int lowerBinStart = (int) (((float) lowerRangeStart / sampleRate) * fftSize);
                int lowerBinEnd = (int) (((float) lowerRangeEnd / sampleRate) * fftSize);
                int upperBinStart = (int) (((float) upperRangeStart / sampleRate) * fftSize);
                int upperBinEnd = (int) (((float) upperRangeEnd / sampleRate) * fftSize);

                float lowerSum = 0;
                float upperSum = 0;

                // Berechne die Summen der Amplituden im jeweiligen Frequenzbereich
                for (int i = lowerBinStart; i < lowerBinEnd; i++) {
                    lowerSum += Math.sqrt(fftDaten[i * 2] * fftDaten[i * 2] + fftDaten[i * 2 + 1] * fftDaten[i * 2 + 1]);
                }
                for (int i = upperBinStart; i < upperBinEnd; i++) {
                    upperSum += Math.sqrt(fftDaten[i * 2] * fftDaten[i * 2] + fftDaten[i * 2 + 1] * fftDaten[i * 2 + 1]);
                }

                // Berechne die durchschnittlichen Amplituden
                float lowerAvg = lowerSum / (lowerBinEnd - lowerBinStart);
                float upperAvg = upperSum / (upperBinEnd - upperBinStart);

                // Skaliere die durchschnittlichen Amplituden, um sie im gewünschten Bereich zu halten
                float lowerRadiusScale = Math.min(1 + lowerAvg / 50, 2);
                float upperRadiusScale = Math.min(1 + upperAvg / 50, 2);

                // Zeichne den Kreis
                float centerX = getWidth() / 2f;
                float centerY = getHeight() / 2f;
                float radius = Math.min(getWidth(), getHeight()) / 4f;

                paint.setStyle(Paint.Style.FILL);

                // Zeichne die obere Hälfte des Kreises
                paint.setColor(Color.rgb(0, 128, 255));
                RectF oval = new RectF(centerX - radius * upperRadiusScale, centerY - radius * upperRadiusScale, centerX + radius * upperRadiusScale, centerY + radius * upperRadiusScale);
                canvas.drawArc(oval, 0, 180, true, paint);

                // Zeichne die untere Hälfte des Kreises
                paint.setColor(Color.rgb(255, 128, 0));
                oval = new RectF(centerX - radius * lowerRadiusScale, centerY - radius * lowerRadiusScale, centerX + radius * lowerRadiusScale, centerY + radius * lowerRadiusScale);
                canvas.drawArc(oval, 180, 180, true, paint);
        }
		
		
		
		
		
		
		//Zwei zuckende Halbkreise
		if (fftDaten != null) {

		            int centerX = getWidth() / 2;
		            int centerY = getHeight() / 2;
		            int radius = Math.min(centerX, centerY) - 10;

		            float upperFrequencyMin = 9000f;
		            float upperFrequencyMax = 9050f;
		            float lowerFrequencyMin = 100f;
		            float lowerFrequencyMax = 150f;

		            float upperRadiusScale = calculateAmplitude(fftDaten, upperFrequencyMin, upperFrequencyMax);
		            float lowerRadiusScale = calculateAmplitude(fftDaten, lowerFrequencyMin, lowerFrequencyMax);

		            upperRadiusScale *= 2.5;
		            lowerRadiusScale *= 1;

		            // Zeichne die obere Hälfte des Kreises
		            int upperAlpha = (int) (255 * upperRadiusScale);
		            paint.setColor(Color.argb(upperAlpha, 255, 0, 0));
		            RectF oval = new RectF(centerX - radius * upperRadiusScale, centerY - radius * upperRadiusScale, centerX + radius * upperRadiusScale, centerY + radius * upperRadiusScale);
		            canvas.drawArc(oval, 180, 180, true, paint);

		            // Zeichne die untere Hälfte des Kreises
		            int lowerAlpha = (int) (255 * lowerRadiusScale);
		            paint.setColor(Color.argb(lowerAlpha, 255, 255, 255));
		            oval = new RectF(centerX - radius * lowerRadiusScale, centerY - radius * lowerRadiusScale, centerX + radius * lowerRadiusScale, centerY + radius * lowerRadiusScale);
		            canvas.drawArc(oval, 0, 180, true, paint);
		        }
		    }

		    private float calculateAmplitude(byte[] fftData, float frequencyMin, float frequencyMax) {
		        int sampleRate = 44100; // Hier sollte die tatsächliche Abtastrate verwendet werden, wenn sie verfügbar ist
		        int fftSize = fftData.length / 2;

		        int indexMin = (int) (frequencyMin / (sampleRate / 2f) * fftSize);
		        int indexMax = (int) (frequencyMax / (sampleRate / 2f) * fftSize);

		        float amplitudeSum = 0;
		        int count = 0;

		        for (int i = indexMin; i < indexMax; i++) {
		            float re = fftData[i * 2];
		            float im = fftData[i * 2 + 1];
		            float magnitude = (float) Math.sqrt(re * re + im * im);
		            amplitudeSum += magnitude;
		            count++;
		        }

		        if (count == 0) {
		            return 0;
		        }

		        float averageAmplitude = amplitudeSum / count;
		        float normalizedAmplitude = Math.min(averageAmplitude / 128, 1);

		        float scaledAmplitude = (float) Math.pow(normalizedAmplitude, 0.3); // Anpassung der Empfindlichkeit durch Exponenten

		        return scaledAmplitude;
		    }
			
			
			
			
			
			//Zwei anders zuckende Halbkreise
			
			public void updateVisualizerFFT(byte[] fftDaten) {
			        updateRateAusgeben();
			        this.fftDaten = fftDaten;
			        invalidate();
			    }

			    @Override
			    protected void onDraw(Canvas canvas) { //Wird automatisch aufgerufen, wenn die View neu gezeichnet werden soll
			        super.onDraw(canvas);

			        if (waveformDaten == null && fftDaten == null) {
			            return;
			        }

			        if (waveformDaten != null) {
			            //Ist das punkteZumZeichnen-Array groß genug für die übergebenen waveform-Daten?
			            //Jedes benachbarte Paar aus den waveform-Daten bedingt 2 Punkte mit je 2 Koordinaten
			            if (punkteZumZeichnen == null || punkteZumZeichnen.length < waveformDaten.length * 4) {
			                punkteZumZeichnen = new float[waveformDaten.length * 4];
			            }

			            viewRect.set(0, 0, getWidth(), getHeight());

			            for (int i = 0, j = 0; i < waveformDaten.length - 1; i++, j += 4) {
			                float left = viewRect.width() * i / (float) (waveformDaten.length - 1);
			                float top = viewRect.height() - ((byte) (waveformDaten[i] + 128)) * viewRect.height() / 128f;
			                float right = viewRect.width() * (i + 1) / (float) (waveformDaten.length - 1);
			                float bottom = viewRect.height() - ((byte) (waveformDaten[i + 1] + 128)) * viewRect.height() / 128f;

			                punkteZumZeichnen[j] = left;
			                punkteZumZeichnen[j + 1] = top;
			                punkteZumZeichnen[j + 2] = right;
			                punkteZumZeichnen[j + 3] = bottom;
			            }
			            //System.out.println(Arrays.toString(waveformDaten));
			            canvas.drawLines(punkteZumZeichnen, paint);
			        }

			        if (fftDaten != null) {

			            int centerX = getWidth() / 2;
			            int centerY = getHeight() / 2;
			            int radius = Math.min(centerX, centerY) - 10;

			            float upperFrequencyMin = 9000f;
			            float upperFrequencyMax = 9050f;
			            float lowerFrequencyMin = 100f;
			            float lowerFrequencyMax = 150f;

			            float upperRadiusScale = calculateAmplitude(fftDaten, upperFrequencyMin, upperFrequencyMax);
			            float lowerRadiusScale = calculateAmplitude(fftDaten, lowerFrequencyMin, lowerFrequencyMax);

			            upperRadiusScale *= 2.5;
			            lowerRadiusScale *= 1;

			            // Zeichne die obere Hälfte des Kreises
			            int upperAlpha = (int) (255 * upperRadiusScale);
			            paint.setColor(Color.argb(upperAlpha, 255, 0, 0));
			            RectF oval = new RectF(centerX - radius * upperRadiusScale, centerY - radius * upperRadiusScale, centerX + radius * upperRadiusScale, centerY + radius * upperRadiusScale);
			            canvas.drawArc(oval, 180, 180, true, paint);

			            // Zeichne die untere Hälfte des Kreises
			            int lowerAlpha = (int) (255 * lowerRadiusScale);
			            paint.setColor(Color.argb(lowerAlpha, 255, 255, 255));
			            oval = new RectF(centerX - radius * lowerRadiusScale, centerY - radius * lowerRadiusScale, centerX + radius * lowerRadiusScale, centerY + radius * lowerRadiusScale);
			            canvas.drawArc(oval, 0, 180, true, paint);
			        }

			    }

			    private float calculateAmplitude(byte[] fftData, float frequencyMin, float frequencyMax) {
			        int sampleRate = 44100; // Hier sollte die tatsächliche Abtastrate verwendet werden, wenn sie verfügbar ist
			        int fftSize = fftData.length / 2;

			        int indexMin = (int) (frequencyMin / (sampleRate / 2f) * fftSize);
			        int indexMax = (int) (frequencyMax / (sampleRate / 2f) * fftSize);

			        float amplitudeSum = 0;
			        int count = 0;

			        for (int i = indexMin; i < indexMax; i++) {
			            float re = fftData[i * 2];
			            float im = fftData[i * 2 + 1];
			            float magnitude = (float) Math.sqrt(re * re + im * im);
			            amplitudeSum += magnitude;
			            count++;
			        }

			        if (count == 0) {
			            return 0;
			        }

			        float averageAmplitude = amplitudeSum / count;
			        float normalizedAmplitude = Math.min(averageAmplitude / 128, 1);

			        float scaledAmplitude = (float) Math.pow(normalizedAmplitude, 0.3); // Anpassung der Empfindlichkeit durch Exponenten

			        return scaledAmplitude;
			    }