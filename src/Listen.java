

import java.io.File;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class Listen {
	
    private static Map<WatchKey, Path> keyPathMap = new HashMap<>();
    public String nameOfFile;
    public String pathOfFile;
    public String actionOnFile;
    	
    /////MAIN
    public  void main (String[] args) throws Exception 
    {
    	
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) 
        {
            registerDir(Paths.get("/Users/hugomounier/Desktop/divers"), watchService);
            startListening(watchService);
        }
    }
    

    private void registerDir (Path path, WatchService watchService) throws IOException 
    {
    	
        if (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) 
        {
            return;
            
        }
        

        System.out.println("registering: " + path);
        
        this.pathOfFile=path.toString();

        WatchKey key = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_MODIFY,
                            StandardWatchEventKinds.ENTRY_DELETE);
        keyPathMap.put(key, path);


        for (File f : path.toFile().listFiles()) 
        {
            registerDir(f.toPath(), watchService);
        }
    }

    private void startListening (WatchService watchService) throws Exception 
    {
        while (true) 
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
                    
                    this.nameOfFile=watchEvent.context().toString();
                    this.actionOnFile=watchEvent.kind().toString();
                }
                
                //notification modify
                
                if (watchEvent.kind() == StandardWatchEventKinds.ENTRY_MODIFY) 
                {
                   
                    Path path = (Path) watchEvent.context();
                  
                    Path parentPath = keyPathMap.get(queuedKey);
                    
                    path = parentPath.resolve(path);

                    registerDir(path, watchService);
                    
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
                break;
            }
        }
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