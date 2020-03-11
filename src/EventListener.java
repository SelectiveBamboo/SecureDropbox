

import java.io.File;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class EventListener {
	
    private static Map<WatchKey, Path> keyPathMap = new HashMap<>();
    public  String nameOfFile;
    public  String pathOfFile;
    public  String actionOnFile;
    	
    public String path;
    
    public EventListener (String path){
    		
    	this.path = path;
    	
    }
    
    public boolean Listen() throws Exception {
    	
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) 
        {
            registerDir(Paths.get(path), watchService);
            return startListening(watchService);
        }
    	
    }
    

    private void registerDir (Path path, WatchService watchService) throws IOException 
    {

        System.out.println("registering: " + path);
        
        
        
        //this.pathOfFile=path.toString();
        
        
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) 
        {
        	
        	
            WatchKey key = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);
            keyPathMap.put(key, path);
        	
        	for (File f : path.toFile().listFiles()) 
            {
                registerDir(f.toPath(), watchService);
            }
        }
        
    }

    private boolean startListening (WatchService watchService) throws Exception 
    {
          WatchKey queuedKey = watchService.take();
            for (WatchEvent<?> watchEvent : queuedKey.pollEvents()) 
            {
                System.out.printf("Event... pathkind=%s, count=%d, context=%s \n",
                		watchEvent.kind(),watchEvent.count(), watchEvent.context());
                
                //System.out.printf("Path: ");
                //System.out.printf("Type=%s",watchEvent.kind());
                //notification creation

                if (watchEvent.kind() == StandardWatchEventKinds.ENTRY_CREATE) 
                {
                  
                    Path path = (Path) watchEvent.context();
                   
                    Path parentPath = keyPathMap.get(queuedKey);
                  
                    path = parentPath.resolve(path);

                    registerDir(path, watchService);
                    
                    this.pathOfFile=path.toString();
                    this.nameOfFile=watchEvent.context().toString();
                    this.actionOnFile=watchEvent.kind().toString();
                }
                
                //notification modify
                
                if (watchEvent.kind() == StandardWatchEventKinds.ENTRY_MODIFY && !watchEvent.context().toString().equals(".DS_Store")) 
                {
                   
                    Path path = (Path) watchEvent.context();
                  
                    Path parentPath = keyPathMap.get(queuedKey);
                    
                    path = parentPath.resolve(path);

                    registerDir(path, watchService);
                    
                    this.pathOfFile=path.toString();
                    this.nameOfFile=watchEvent.context().toString();
                    this.actionOnFile=watchEvent.kind().toString();
                   
                }
                
                //notification delete
                
                if (watchEvent.kind() == StandardWatchEventKinds.ENTRY_DELETE) 
                {

                	Path path = (Path) watchEvent.context();
                   
                    Path parentPath = keyPathMap.get(queuedKey);
                    
                    path = parentPath.resolve(path);

                    registerDir(path, watchService);
                    
                    this.pathOfFile=path.toString();                    
                    this.nameOfFile=watchEvent.context().toString();
                    this.actionOnFile=watchEvent.kind().toString();
                }
            }
            
            if (!queuedKey.reset()) 
            {
                keyPathMap.remove(queuedKey);
            }
            if (keyPathMap.isEmpty()) 
            {
                return false;
            }
            return true;
        }
    


	public String getNameOfFile() {
		return nameOfFile;
	}


	public String getPathOfFile() {
		return pathOfFile;
	}


	public String getActionOnFile() {
		return actionOnFile;
	}


}