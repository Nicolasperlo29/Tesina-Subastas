import { jsPDF } from 'jspdf';
import autoTable from 'jspdf-autotable';
import { ReportePujas } from './reporte-pujas'; 

export function exportarReportePujasPDF(reporte: ReportePujas[]) {
  const doc = new jsPDF();

  doc.text('Reporte de Pujas por Usuario', 14, 15);

  autoTable(doc, {
    startY: 20,
    head: [['Usuario', 'Cantidad', 'Total', 'Promedio']],
    body: reporte.map(r => [
      r.userId,
      r.cantidadPujas,
      `$${r.totalOfertado.toFixed(2)}`,
      `$${r.promedioOfertado.toFixed(2)}`
    ])
  });

  doc.save('reporte-pujas.pdf');
}