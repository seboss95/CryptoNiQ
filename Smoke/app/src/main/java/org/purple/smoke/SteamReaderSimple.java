/*
** Copyright (c) Alexis Megas.
** All rights reserved.
**
** Redistribution and use in source and binary forms, with or without
** modification, are permitted provided that the following conditions
** are met:
** 1. Redistributions of source code must retain the above copyright
**    notice, this list of conditions and the following disclaimer.
** 2. Redistributions in binary form must reproduce the above copyright
**    notice, this list of conditions and the following disclaimer in the
**    documentation and/or other materials provided with the distribution.
** 3. The name of the author may not be used to endorse or promote products
**    derived from Smoke without specific prior written permission.
**
** SMOKE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
** IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
** OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
** IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
** INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
** NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
** DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
** THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
** (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
** SMOKE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.purple.smoke;

import java.io.FileInputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SteamReaderSimple
{
    private AtomicBoolean m_completed = null;
    private AtomicBoolean m_paused = null;
    private AtomicBoolean m_read = null;
    private AtomicInteger m_offset = null;
    private AtomicInteger m_rate = null;
    private ScheduledExecutorService m_reader = null;
    private String m_fileName = "";
    private static int PACKET_SIZE = 4096;
    private static long READ_INTERVAL = 250; // 250 Milliseconds

    private void prepareReader()
    {
	if(m_reader == null)
	{
	    m_reader = Executors.newSingleThreadScheduledExecutor();
	    m_reader.scheduleAtFixedRate(new Runnable()
	    {
		@Override
		public void run()
		{
		    FileInputStream fileInputStream = null;

		    try
		    {
			if(m_completed.get() || m_paused.get())
			    return;

			fileInputStream = new FileInputStream(m_fileName);

			if(fileInputStream == null)
			    return;

			byte bytes[] = new byte[PACKET_SIZE];
			int offset = fileInputStream.
			    read(bytes, m_offset.get(), bytes.length);

			if(offset == -1)
			    m_completed.set(true);
			else
			    m_offset.addAndGet(offset);

			/*
			** Send raw bytes.
			*/
		    }
		    catch(Exception exception)
		    {
		    }
		    finally
		    {
			try
			{
			    if(fileInputStream != null)
				fileInputStream.close();
			}
			catch(Exception exception)
			{
			}
		    }
		}
	    }, 1500, READ_INTERVAL, TimeUnit.MILLISECONDS);
	}
    }

    public SteamReaderSimple(String fileName)
    {
	m_completed = new AtomicBoolean(false);
	m_fileName = fileName;
	m_offset = new AtomicInteger(0);
	m_paused = new AtomicBoolean(false);
	m_rate = new AtomicInteger(0);
    }

    public void cancel()
    {
	m_completed.set(true);
	m_offset.set(0);
	m_paused.set(true);
	m_rate.set(0);

	if(m_reader != null)
	{
	    try
	    {
		m_reader.shutdown();
	    }
	    catch(Exception exception)
	    {
	    }

	    try
	    {
		if(!m_reader.awaitTermination(60, TimeUnit.SECONDS))
		    m_reader.shutdownNow();
	    }
	    catch(Exception exception)
	    {
	    }
	    finally
	    {
		m_reader = null;
	    }
	}
    }

    public void setPaused(boolean state)
    {
	m_paused.set(state);
	m_rate.set(0);
    }
}