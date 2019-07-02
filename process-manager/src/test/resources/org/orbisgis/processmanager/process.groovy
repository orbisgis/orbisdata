import org.locationtech.jts.geom.Geometry
import org.orbisgis.processmanager.ProcessManager
import org.orbisgis.processmanager.inoutput.Input
import org.orbisgis.processmanager.inoutput.Output
import org.orbisgis.processmanagerapi.IProcess

def input = {Input.create()}
def output = { Output.create()}

IProcess p = ProcessManager.processManager.create()
        .title("Buffer")
        .description("Buffers a geometry")
        .inputs([
                geom: input().title("The geometry to buffer").type(Geometry),
                distance: input().title("The buffer distance").type(Double)
        ])
        .outputs([
                result: output().title("The buffered geometry").type(Geometry),
        ])
        .closure { geom, distance ->
            return [result: geom.buffer(distance)]
        }
        .process

println p.toWps("geoserver")
