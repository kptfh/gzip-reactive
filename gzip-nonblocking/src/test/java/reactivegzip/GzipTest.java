/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package reactivegzip;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Copied from Spring's Jackson2TokenizerTest
 */
public class GzipTest {

	@Test
	public void shouldDecodeEncoded(){

		NonblockingGzipEncoder encoder = new NonblockingGzipEncoder(false);
		NonblockingGzipDecoder decoder = new NonblockingGzipDecoder(5);

		ByteBuffer data = ByteBuffer.wrap("Hello Gzip".getBytes());
		ByteBuffer dataEncodedDecoded = decoder.decode(encoder.encode(data));
		for(int i = 0; i < 100; i++){
			ByteBuffer chunk = ByteBuffer.wrap(((i - 2) + "Hello Gzip " + i).getBytes());
			ByteBuffer decoded = decoder.decode(encoder.encode(chunk));
			data = concat(data, chunk);
			dataEncodedDecoded = concat(dataEncodedDecoded, decoded);
		}

		dataEncodedDecoded = concat(dataEncodedDecoded, decoder.decode(encoder.endOfInput()));

		assertThat(dataEncodedDecoded).isEqualTo(data);

	}

	private static ByteBuffer concat(ByteBuffer data1, ByteBuffer data2){
		return (ByteBuffer)ByteBuffer.allocate(data1.limit() + data2.limit()).put(data1).put(data2).position(0);
	}


}
