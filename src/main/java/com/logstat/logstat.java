package com.logstat;
import java.util.*;
import java.text.*;
import java.io.*;

public class logstat {

    public static void main(String []args) throws IOException, ParseException {

        int i,d,n,iFile;

        // выбор интервала (1=часы, !1=минуты)
        int interval = 1; 
    
        // каким-то образом нам предоставляется список логов
        String[] paths = {
             "log_01.txt",
             "log_02.txt",
             "log_03.txt"
        };
	   
	// файл результата    
        String outpath = "logstat.txt";
        
        
        ArrayList tms = new ArrayList();  // список временных меток
        ArrayList errs = new ArrayList(); // список счетчиков ошибок
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); // парсер даты создаем вне цикла

        
        // пробегаемся по файлам и выписываем ошибки в общий список
        for (iFile = 0; iFile < paths.length; iFile++){
            
            
            File file = new File(paths[iFile]);
            if (!file.exists()) continue;          // файла нет - нереходим к следующему
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
           
            while (true) {
                String line = reader.readLine(); 
                if (line == null) break;         
                if (line.trim() == "") continue;          // пустая строка - пропускаем
                String[] parts = line.split(";");         
                if (parts.length < 3) continue;           // некорректная строка лога - пропускаем
                if (!parts[1].trim().equals("ERROR") ) continue; // тип записи не ERROR - пропускаем            				
				
                
                Date date = df.parse(parts[0].trim());
                long msec = date.getTime(); // миллисекунды с начала времен
                long sec = msec/1000; // целых секунд с начала времен
				long min = sec/60;    // целых минут с начала времен
                long hrs = min/60;    // целых часов с начала времен
				
                // определяем номер часа/минуты в зависимости от выбранного интервала
                Long time = min;
                if (interval == 1) time = hrs;

			
                // ищем это время в нашем списке
                n = tms.indexOf(time);

                if(n == -1){ // если нет - добавляем
                    tms.add(time);
                    errs.add(new Integer(1));    
                } else { // если есть - увеличиваем соответствующий счетчик
                    Integer count = (Integer)errs.get(n);
                    count++;
                    errs.set(n,count);    
                }
                
            }            
        }
        

        // после добавления записей из всех файлов 
        // определяем порядок, в котором они будут выводиться
        ArrayList srt = new ArrayList(tms);
        Collections.sort(srt);

        // выводим записи 
        FileWriter writer = new FileWriter(outpath, false);
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy.MM.dd;  HH:mm:ss"); // парсер даты создаем вне цикла

		
        for(i = 0; i < srt.size(); i++){    

            n = tms.indexOf(srt.get(i));
            Long time = (Long)tms.get(n);
			
			
            if (interval == 1) time *= 3600; else time*=60; // переводим в секунды в зависимости от выбранного интервала
			
			Date date = new Date(time*1000); // создаем дату (из миллисекунд)
        
            String s1 = df2.format(date) + "; errors: "+errs.get(n);

            writer.write(s1+"\n");

        }


        writer.flush();
        

    }
}






