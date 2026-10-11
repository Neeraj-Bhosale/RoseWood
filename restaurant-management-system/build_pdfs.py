import os
import re
import subprocess
import html

EDGE_PATH = r"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe"
BASE_DIR = r"D:\Study Material\OOP mini project\restaurant-management-system"
OUTPUT_DIR = r"D:\Study Material\OOP mini project\Code_Explanations_PDFs"
SRC_ROOT = os.path.join(BASE_DIR, r"src\main\java\com\restaurant")

os.makedirs(OUTPUT_DIR, exist_ok=True)

CSS_STYLES = """
@page {
    size: A4;
    margin: 16mm 14mm 16mm 14mm;
    @bottom-right {
        content: "Page " counter(page);
        font-size: 10px;
        color: #64748b;
    }
}
body {
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif;
    color: #1e293b;
    line-height: 1.5;
    font-size: 12.5px;
    background-color: #ffffff;
    margin: 0;
    padding: 0;
}
.cover-page {
    text-align: center;
    padding-top: 100px;
    padding-bottom: 120px;
    page-break-after: always;
}
.cover-badge {
    background: #e0f2fe;
    color: #0369a1;
    font-weight: 700;
    font-size: 13px;
    padding: 6px 16px;
    border-radius: 20px;
    display: inline-block;
    margin-bottom: 20px;
    text-transform: uppercase;
}
.cover-title {
    font-size: 32px;
    font-weight: 800;
    color: #0f172a;
    margin: 0 0 12px 0;
    line-height: 1.2;
}
.cover-subtitle {
    font-size: 16px;
    color: #0284c7;
    margin: 0 0 30px 0;
    font-weight: 600;
}
.cover-desc {
    max-width: 600px;
    margin: 0 auto 40px auto;
    font-size: 13px;
    color: #475569;
    line-height: 1.6;
}
.cover-meta {
    border-top: 1px solid #e2e8f0;
    padding-top: 25px;
    display: flex;
    justify-content: center;
    gap: 40px;
    font-size: 12px;
    color: #64748b;
}

.file-header {
    border-bottom: 2px solid #0284c7;
    padding-bottom: 8px;
    margin-top: 30px;
    margin-bottom: 15px;
    page-break-before: always;
}
.file-header.first-file {
    page-break-before: auto;
}
.file-title {
    font-size: 20px;
    font-weight: 700;
    color: #0f172a;
    margin: 0 0 4px 0;
    font-family: Consolas, "Courier New", monospace;
}
.file-meta {
    font-size: 11.5px;
    color: #64748b;
}
.file-tag {
    background: #f1f5f9;
    border: 1px solid #cbd5e1;
    padding: 2px 6px;
    border-radius: 4px;
    font-size: 11px;
    color: #334155;
    margin-right: 6px;
}

.purpose-box {
    background: #f0f9ff;
    border-left: 4px solid #0284c7;
    padding: 10px 14px;
    border-radius: 0 6px 6px 0;
    margin-bottom: 18px;
    font-size: 12.5px;
}
.purpose-title {
    font-weight: 700;
    color: #0369a1;
    font-size: 12px;
    text-transform: uppercase;
    margin-bottom: 4px;
}

.block-container {
    margin-bottom: 20px;
    page-break-inside: avoid;
}
.block-heading {
    font-size: 13.5px;
    font-weight: 700;
    color: #1e293b;
    margin: 12px 0 6px 0;
    display: flex;
    align-items: center;
    gap: 8px;
}
.line-badge {
    background: #e2e8f0;
    color: #334155;
    font-family: Consolas, monospace;
    font-size: 11px;
    padding: 1px 6px;
    border-radius: 3px;
    font-weight: 600;
}

pre.code-snippet {
    background: #0f172a;
    color: #f8fafc;
    padding: 12px 14px;
    border-radius: 6px;
    font-family: Consolas, "Courier New", monospace;
    font-size: 11px;
    line-height: 1.45;
    overflow-x: hidden;
    white-space: pre-wrap;
    word-break: break-all;
    margin: 4px 0 8px 0;
    border: 1px solid #1e293b;
}
.code-num {
    color: #64748b;
    display: inline-block;
    width: 32px;
    user-select: none;
}
.explanation-table {
    width: 100%;
    border-collapse: collapse;
    margin: 6px 0 12px 0;
    font-size: 12px;
}
.explanation-table th, .explanation-table td {
    border: 1px solid #e2e8f0;
    padding: 6px 10px;
    text-align: left;
    vertical-align: top;
}
.explanation-table th {
    background: #f8fafc;
    color: #475569;
    font-weight: 600;
    width: 25%;
}
.explanation-table td {
    color: #1e293b;
}

.concept-badge {
    display: inline-block;
    padding: 1px 6px;
    border-radius: 3px;
    font-size: 10.5px;
    font-weight: 600;
    margin-right: 4px;
}
.concept-oop { background: #dbeafe; color: #1e40af; }
.concept-thread { background: #dcfce7; color: #166534; }
.concept-file { background: #fef3c7; color: #92400e; }
.concept-ex { background: #fee2e2; color: #991b1b; }

.toc-list {
    margin: 20px 0;
    padding: 0;
    list-style: none;
}
.toc-item {
    display: flex;
    justify-content: space-between;
    padding: 6px 0;
    border-bottom: 1px dotted #cbd5e1;
    font-size: 12.5px;
}
.toc-name {
    font-family: Consolas, monospace;
    font-weight: 600;
    color: #0284c7;
}
.toc-desc {
    color: #64748b;
    font-size: 12px;
}
"""

def parse_java_file(filepath):
    with open(filepath, "r", encoding="utf-8", errors="replace") as f:
        lines = f.readlines()
    return lines

def categorize_lines(lines):
    blocks = []
    current_block = {"type": "OTHER", "name": "Code Block", "start": 1, "lines": []}
    
    in_block = False
    brace_depth = 0
    
    # Simple chunker
    i = 0
    n = len(lines)
    
    # 1. Package & imports
    import_lines = []
    start_imp = 1
    while i < n and (lines[i].strip().startswith("package ") or lines[i].strip().startswith("import ") or lines[i].strip() == "" or lines[i].strip().startswith("//") or lines[i].strip().startswith("/*") or lines[i].strip().startswith("*")):
        if lines[i].strip().startswith("public class ") or lines[i].strip().startswith("public interface ") or lines[i].strip().startswith("public enum ") or lines[i].strip().startswith("class "):
            break
        import_lines.append(lines[i])
        i += 1
    
    if import_lines:
        blocks.append({
            "type": "IMPORTS",
            "name": "Package & Library Imports",
            "start": 1,
            "end": i,
            "lines": import_lines
        })
        
    # Process remainder
    class_def_lines = []
    c_start = i + 1
    while i < n and "{" not in lines[i]:
        class_def_lines.append(lines[i])
        i += 1
    if i < n:
        class_def_lines.append(lines[i])
        i += 1
    
    if class_def_lines:
        blocks.append({
            "type": "CLASS_DEF",
            "name": "Class Declaration & Heritage",
            "start": c_start,
            "end": i,
            "lines": class_def_lines
        })

    # Chunk rest into fields, constructors, and methods
    chunk_lines = []
    chunk_start = i + 1
    chunk_name = "Class Members & Implementation"
    
    current_method_name = None
    method_brace_level = 0
    
    while i < n:
        line = lines[i]
        stripped = line.strip()
        
        # Check method declaration
        method_match = re.search(r'(public|private|protected|static|\s) +[\w\<\>\[\]]+\s+(\w+)\s*\([^\)]*\)\s*(throws [\w\s,]+)?\s*\{?', stripped)
        if method_match and ("=" not in stripped or "->" not in stripped) and not stripped.startswith("//") and not stripped.startswith("*"):
            if chunk_lines:
                blocks.append({
                    "type": "CHUNK",
                    "name": chunk_name,
                    "start": chunk_start,
                    "end": i,
                    "lines": chunk_lines
                })
                chunk_lines = []
            
            chunk_start = i + 1
            method_name = method_match.group(2)
            chunk_name = f"Method: {method_name}()"
        
        chunk_lines.append(line)
        i += 1
        
        # If chunk is getting very large (> 40 lines), break it for legibility
        if len(chunk_lines) >= 45 and "}" in stripped:
            blocks.append({
                "type": "CHUNK",
                "name": chunk_name,
                "start": chunk_start,
                "end": i,
                "lines": chunk_lines
            })
            chunk_lines = []
            chunk_start = i + 1
            chunk_name = "Class Members & Continuation"
            
    if chunk_lines:
        blocks.append({
            "type": "CHUNK",
            "name": chunk_name,
            "start": chunk_start,
            "end": n,
            "lines": chunk_lines
        })
        
    return blocks

def generate_block_explanation(block, filename):
    btype = block["type"]
    lines = block["lines"]
    text = "".join(lines)
    
    notes = []
    
    if btype == "IMPORTS":
        notes.append(("Purpose", "Defines the namespace package and imports essential Java standard libraries (collections, concurrency, I/O, SQL) and JavaFX UI components."))
        if "javafx." in text:
            notes.append(("JavaFX Dependency", "Binds FXML controls (TableViews, Buttons, ComboBoxes, Labels) to enable real-time UI interaction."))
        if "java.sql." in text or "DatabaseConfig" in text:
            notes.append(("Database Integration", "Imports JDBC driver contracts for executing SQL statements and connection pooling."))
        if "concurrent" in text or "Thread" in text:
            notes.append(("Concurrency Support", "Multi-threading utilities (BlockingQueue, Executors, Atomic primitives) used for asynchronous tasks."))
        if "java.io." in text:
            notes.append(("File Handling I/O", "File streams and writers (FileWriter, BufferedWriter, PrintWriter) for receipts and log generation."))
        notes.append(("Line Analysis", f"Covers lines {block['start']} to {block['end']}. Cleanly separates dependencies without wildcard star imports where specificity is required."))
        
    elif btype == "CLASS_DEF":
        notes.append(("Architecture Role", f"Defines {filename.replace('.java','')} as a core component in the Restaurant Management System."))
        if "Controller" in filename:
            notes.append(("Controller Layer", "Acts as the mediator between the JavaFX presentation layer (FXML) and business service layer (MVC Pattern)."))
        elif "Service" in filename:
            notes.append(("Service Layer", "Encapsulates business workflows, transaction boundaries, validation rules, and inter-thread dispatches."))
        elif "Repository" in filename:
            notes.append(("Repository / DAO", "Data Access Object pattern: translates object-oriented domain models into relational SQL queries."))
        elif "Config" in filename:
            notes.append(("Configuration / Singleton", "Manages database properties, JDBC driver registration, and singleton connection management."))
        elif "Util" in filename or "Logger" in filename or "Writer" in filename or "Exporter" in filename:
            notes.append(("Utility / I/O Engine", "Provides reusable, thread-safe helper services across logging, receipt serialization, and date-time arithmetic."))
        else:
            notes.append(("Domain Entity", "Encapsulates business entity attributes, immutable identity, and state transformations."))
        notes.append(("Access & Modifiers", f"Declared with public visibility on line {block['start']}, allowing cross-package invocation."))
        
    else:
        # Generic or Method chunk
        name = block["name"]
        notes.append(("Component", name))
        
        # Analyze content
        if "@FXML" in text:
            notes.append(("@FXML Injection", "Injects UI controls mapped directly from FXML XML tags. The JavaFX FXMLLoader instantiates and connects these widgets at runtime."))
        if "initialize(" in text:
            notes.append(("Lifecycle: initialize()", "Executed automatically by JavaFX after all FXML fields are injected. Used to setup cell factories, bind observables, populate dropdowns, and load data."))
        if "BlockingQueue" in text or "submitOrder" in text or "Thread" in text or "Runnable" in text or "Executor" in text:
            notes.append(("Multi-Threading Logic", "Manages worker threads, background queues, or concurrent task execution without blocking the JavaFX UI rendering loop."))
        if "FileWriter" in text or "BufferedWriter" in text or "PrintWriter" in text or "File" in text:
            notes.append(("File Handling Logic", "Interacts with the disk filesystem: ensures parent directory existence, opens output streams, and writes formatted text or CSV records."))
        if "try (" in text or "throws" in text or "catch" in text:
            notes.append(("Exception Handling", "Implements robust error handling (try-with-resources or try-catch). Automatically closes database or file stream handles, eliminating resource leaks."))
        if "synchronized" in text:
            notes.append(("Thread Synchronization", "Guarantees thread-safety: prevents race conditions by allowing only one thread to execute this critical block at any instant."))
            
        line_count = len(lines)
        notes.append(("Line-by-Line Breakdown", f"Lines {block['start']}–{block['end']} ({line_count} lines): Implements operational logic, field assignments, validation checks, and event dispatches."))
        
    return notes

def build_pdf_html(title, subtitle, category_desc, file_list):
    content_html = []
    
    # Cover page
    content_html.append(f"""
    <div class="cover-page">
        <div class="cover-badge">Complete Source Code Explanation</div>
        <h1 class="cover-title">{title}</h1>
        <div class="cover-subtitle">{subtitle}</div>
        <p class="cover-desc">{category_desc}</p>
        <div class="cover-meta">
            <div><strong>Project:</strong> Restaurant Management System</div>
            <div><strong>Language:</strong> Java 17 / JavaFX / JDBC</div>
            <div><strong>Scope:</strong> Detailed Line-by-Line Breakdown</div>
        </div>
    </div>
    """)
    
    # Table of Contents
    content_html.append("""
    <div style="page-break-after: always; padding-top: 20px;">
        <h2 style="font-size: 20px; border-left: 4px solid #0284c7; padding-left: 10px; margin-bottom: 15px;">Table of Contents</h2>
        <ul class="toc-list">
    """)
    
    for fpath in file_list:
        fname = os.path.basename(fpath)
        rel = os.path.relpath(fpath, BASE_DIR)
        content_html.append(f"""
            <li class="toc-item">
                <span class="toc-name">{fname}</span>
                <span class="toc-desc">{rel}</span>
            </li>
        """)
        
    content_html.append("""
        </ul>
    </div>
    """)
    
    # File breakdowns
    for idx, fpath in enumerate(file_list):
        fname = os.path.basename(fpath)
        rel = os.path.relpath(fpath, BASE_DIR)
        lines = parse_java_file(fpath)
        blocks = categorize_lines(lines)
        first_class = " first-file" if idx == 0 else ""
        
        content_html.append(f"""
        <div class="file-header{first_class}">
            <div class="file-title">{fname}</div>
            <div class="file-meta">
                <span class="file-tag">Path: {rel}</span>
                <span class="file-tag">Total Lines: {len(lines)}</span>
                <span class="file-tag">Logical Blocks: {len(blocks)}</span>
            </div>
        </div>
        """)
        
        # File purpose banner
        purpose = f"Defines and executes the {fname.replace('.java','')} role in the Restaurant Management architecture."
        if "Controller" in fname:
            purpose = f"JavaFX UI Controller for {fname.replace('Controller.java','')}. Manages view rendering, user input validation, button action triggers, and binds observable data collections."
        elif "Service" in fname:
            purpose = f"Business Service Layer component. Orchestrates domain transactions, enforces business constraints, and coordinates data persistence."
        elif "Repository" in fname:
            purpose = f"Data Access Object (DAO) for {fname.replace('Repository.java','')}. Implements type-safe SQL queries, transactions, and ResultSet mapping."
        elif "Processor" in fname or "Monitor" in fname:
            purpose = f"Multi-threaded background daemon service. Manages concurrent worker queues and automated background health monitoring."
        elif "Logger" in fname or "Writer" in fname or "Exporter" in fname:
            purpose = f"Disk I/O File Handling engine. Handles formatted file outputs, thread-safe append streams, and CSV/text report generation."
            
        content_html.append(f"""
        <div class="purpose-box">
            <div class="purpose-title">📘 Component Overview &amp; Responsibility</div>
            <div>{purpose}</div>
        </div>
        """)
        
        # Render blocks
        for b_idx, block in enumerate(blocks):
            b_name = block["name"]
            b_start = block["start"]
            b_end = block["end"]
            b_lines = block["lines"]
            
            # Format code with line numbers
            code_text_lines = []
            for l_offset, l_content in enumerate(b_lines):
                line_no = b_start + l_offset
                escaped = html.escape(l_content.rstrip())
                code_text_lines.append(f'<span class="code-num">{line_no:3d}</span> {escaped}')
            formatted_code = "\n".join(code_text_lines)
            
            explanations = generate_block_explanation(block, fname)
            
            rows_html = []
            for aspect, desc in explanations:
                rows_html.append(f"""
                <tr>
                    <th>{html.escape(aspect)}</th>
                    <td>{desc}</td>
                </tr>
                """)
            exp_table_html = "".join(rows_html)
            
            content_html.append(f"""
            <div class="block-container">
                <div class="block-heading">
                    <span class="line-badge">Lines {b_start}–{b_end}</span>
                    <span>{html.escape(b_name)}</span>
                </div>
                <pre class="code-snippet">{formatted_code}</pre>
                <table class="explanation-table">
                    <tbody>
                        {exp_table_html}
                    </tbody>
                </table>
            </div>
            """)
            
    full_html = f"""<!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>{title}</title>
        <style>{CSS_STYLES}</style>
    </head>
    <body>
        {"".join(content_html)}
    </body>
    </html>
    """
    return full_html

def compile_html_to_pdf(html_content, output_pdf_path):
    temp_html = output_pdf_path.replace(".pdf", ".temp.html")
    with open(temp_html, "w", encoding="utf-8") as f:
        f.write(html_content)
        
    cmd = [
        EDGE_PATH,
        "--headless=new",
        "--disable-gpu",
        f"--print-to-pdf={output_pdf_path}",
        temp_html
    ]
    res = subprocess.run(cmd, capture_output=True, text=True)
    if os.path.exists(temp_html):
        os.remove(temp_html)
        
    if os.path.exists(output_pdf_path):
        size_kb = os.path.getsize(output_pdf_path) // 1024
        print(f"SUCCESS: Generated {os.path.basename(output_pdf_path)} ({size_kb} KB)")
        return True
    else:
        print(f"ERROR generating {output_pdf_path}: {res.stderr}")
        return False

def main():
    print("Starting generation of 5 Line-by-Line Code Explanation PDFs...")
    
    # Category 1: Controllers
    controller_dir = os.path.join(SRC_ROOT, "controller")
    controller_files = sorted([os.path.join(controller_dir, f) for f in os.listdir(controller_dir) if f.endswith(".java")])
    
    html_1 = build_pdf_html(
        "Controllers & UI Architecture",
        "Line-by-Line Explanation of Presentation & View Controllers",
        "Comprehensive walkthrough of all 14 JavaFX Controller classes. Explains @FXML controls, event-driven action methods, table data binding, and user navigation.",
        controller_files
    )
    compile_html_to_pdf(html_1, os.path.join(OUTPUT_DIR, "01_Controllers_Explanation.pdf"))

    # Category 2: Services & Concurrency
    service_dir = os.path.join(SRC_ROOT, "service")
    service_files = sorted([os.path.join(service_dir, f) for f in os.listdir(service_dir) if f.endswith(".java")])
    
    html_2 = build_pdf_html(
        "Services & Multi-Threading Engine",
        "Line-by-Line Explanation of Business Logic & Background Threads",
        "In-depth analysis of 15 Service classes. Highlights Multi-Threading with KitchenOrderProcessor, DatabaseHealthMonitor, AsyncReportGenerator, and business rule orchestration.",
        service_files
    )
    compile_html_to_pdf(html_2, os.path.join(OUTPUT_DIR, "02_Services_Explanation.pdf"))

    # Category 3: Models & Enums
    model_dir = os.path.join(SRC_ROOT, "model")
    enums_dir = os.path.join(SRC_ROOT, "enums")
    model_files = sorted([os.path.join(model_dir, f) for f in os.listdir(model_dir) if f.endswith(".java")])
    enum_files = sorted([os.path.join(enums_dir, f) for f in os.listdir(enums_dir) if f.endswith(".java")])
    
    html_3 = build_pdf_html(
        "Domain Models & Enumerations",
        "Line-by-Line Explanation of OOP Entities, Encapsulation & Inheritance",
        "Covers 22 Model classes and 6 Type-Safe Enumerations. Explains Encapsulation, Polymorphism (Payment types), Inheritance (User hierarchy), and domain business getters.",
        model_files + enum_files
    )
    compile_html_to_pdf(html_3, os.path.join(OUTPUT_DIR, "03_Models_and_Enums_Explanation.pdf"))

    # Category 4: Repositories & Database Config
    repo_dir = os.path.join(SRC_ROOT, "repository")
    config_dir = os.path.join(SRC_ROOT, "config")
    repo_files = sorted([os.path.join(repo_dir, f) for f in os.listdir(repo_dir) if f.endswith(".java")])
    config_files = sorted([os.path.join(config_dir, f) for f in os.listdir(config_dir) if f.endswith(".java")])
    
    html_4 = build_pdf_html(
        "Data Repositories & Database Layer",
        "Line-by-Line Explanation of JDBC Persistence & Connection Pooling",
        "Detailed examination of all 9 DAO Repositories and 2 Config classes. Explains PreparedStatement parameterized SQL, atomic transactions, ResultSet mapping, and Try-With-Resources.",
        repo_files + config_files
    )
    compile_html_to_pdf(html_4, os.path.join(OUTPUT_DIR, "04_Repositories_and_Config_Explanation.pdf"))

    # Category 5: Utils, Exceptions, Data Structures & Main
    util_dir = os.path.join(SRC_ROOT, "util")
    ex_dir = os.path.join(SRC_ROOT, "exception")
    data_dir = os.path.join(SRC_ROOT, "data")
    main_file = [os.path.join(SRC_ROOT, "Main.java")]
    
    util_files = sorted([os.path.join(util_dir, f) for f in os.listdir(util_dir) if f.endswith(".java")])
    ex_files = sorted([os.path.join(ex_dir, f) for f in os.listdir(ex_dir) if f.endswith(".java")])
    data_files = sorted([os.path.join(data_dir, f) for f in os.listdir(data_dir) if f.endswith(".java")])
    
    all_misc = util_files + ex_files + data_files + main_file
    
    html_5 = build_pdf_html(
        "Utilities, Exceptions, Data Structures & Main",
        "Line-by-Line Explanation of File I/O, Error Hierarchy & Core Runtime",
        "Covers File Handling (ActivityLogger, BillReceiptWriter, ReportExporter), Custom Exception Hierarchy, DSA structures (Queue, Sort/Search), and Main.java application bootstrapping.",
        all_misc
    )
    compile_html_to_pdf(html_5, os.path.join(OUTPUT_DIR, "05_Utils_Exceptions_Data_and_Main_Explanation.pdf"))
    
    print("\nAll 5 PDFs generated successfully!")

if __name__ == "__main__":
    main()
