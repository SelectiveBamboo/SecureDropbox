

import java.io.File;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class Listen {
	
    private static Map<WatchKey, Path> keyPathMap = new HashMap<>();
    public static String nameOfFile;
    public static String pathOfFile;
    public static String actionOnFile;
    	
    /////MAIN
    /*public  void main (String[] args) throws Exception 
    {
    	
    	
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) 
        {
            registerDir(Paths.get("/Users/hugomounier/Desktop/divers"), watchService);
            startListening(watchService);
        }
    }
    */
    

    private void registerDir (Path path, WatchService watchService) throws IOException 
    
    
   {
    	WatchService watchService1 = FileSystems.getDefault().newWatchService();
    	
    	Path path1 = Paths.get("/Users/hugomounier/Desktop/divers");
    	
        if (!Files.isDirectory(path1, LinkOption.NOFOLLOW_LINKS)) 
        {
            return;
            
        }
        

        //System.out.println("registering: " + path);
        
        Listen.pathOfFile=path1.toString();

        WatchKey key = path1.register(watchService1, StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_MODIFY,
                            StandardWatchEventKinds.ENTRY_DELETE);
        keyPathMap.put(key, path1);


        for (File f : path1.toFile().listFiles()) 
        {
            registerDir(f.toPath(), watchService1);
        }
    }

    private void startListening (WatchService watchService) throws Exception 
    {
         
        
            WatchKey queuedKey = watchService.take();
            for (WatchEvent<?> watchEvent : queuedKey.pollEvents()) 
            {
                //System.out.printf("Event... pathkind=%s, count=%d, context=%s \n",
                		//watchEvent.kind(),watchEvent.count(), watchEvent.context());
                
                //System.out.printf("Path: ");
                //System.out.printf("Type=%s",watchEvent.kind());
                //notification creation

                if (watchEvent.kind() == StandardWatchEventKinds.ENTRY_CREATE) 
                {
                  
                    Path path = (Path) watchEvent.context();
                   
                    Path parentPath = keyPathMap.get(queuedKey);
                  
                    path = parentPath.resolve(path);

                    registerDir(path, watchService);
                    
                    Listen.nameOfFile=watchEvent.context().toString();
                    Listen.actionOnFile=watchEvent.kind().toString();
                }
                
                //notification modify
                
                if (watchEvent.kind() == StandardWatchEventKinds.ENTRY_MODIFY) 
                {
                   
                    Path path = (Path) watchEvent.context();
                  
                    Path parentPath = keyPathMap.get(queuedKey);
                    
                    path = parentPath.resolve(path);

                    registerDir(path, watchService);
                    
                    Listen.nameOfFile=watchEvent.context().toString();
                    Listen.actionOnFile=watchEvent.kind().toString();
                   
                }
                
                //notification delete
                
                if (watchEvent.kind() == StandardWatchEventKinds.ENTRY_DELETE) 
                {

                	Path path = (Path) watchEvent.context();
                   
                    Path parentPath = keyPathMap.get(queuedKey);
                    
                    path = parentPath.resolve(path);

                    registerDir(path, watchService);
                    
                    Listen.nameOfFile=watchEvent.context().toString();
                    Listen.actionOnFile=watchEvent.kind().toString();
                }
            }
            
            if (!queuedKey.reset()) 
            {
                keyPathMap.remove(queuedKey);
            }
            if (keyPathMap.isEmpty()) 
            {
                return;
            }
        }
    


	public static String getNameOfFile() {
		return nameOfFile;
	}


	public static String getPathOfFile() {
		return pathOfFile;
	}


	public static String getActionOnFile() {
		return actionOnFile;
	}


}