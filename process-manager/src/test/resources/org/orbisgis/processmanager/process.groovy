import org.orbisgis.processmanager.ProcessManager
import org.orbisgis.processmanager.inoutput.Input
import org.orbisgis.processmanagerapi.IProcess

def input = {Input.create()}

IProcess p = ProcessManager.processManager.create()
        .inputs([
                in1:String,
                in2: input().title("input 2").type(String).mandatory()
        ])
        .outputs([
                out:String,
        ])
        .closure { in1, in2 ->
            return [out: in1+in2]
        }
        .process

println p.toWps("geoserver")
